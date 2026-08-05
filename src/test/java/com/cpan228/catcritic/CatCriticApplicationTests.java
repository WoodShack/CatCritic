package com.cpan228.catcritic;

import com.cpan228.catcritic.controller.CatController;
import com.cpan228.catcritic.model.Cat;
import com.cpan228.catcritic.model.Role;
import com.cpan228.catcritic.model.User;
import com.cpan228.catcritic.service.CatService;
import com.cpan228.catcritic.service.RatingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Note: the "unauthenticated users get redirected to /login" requirement is now enforced
 * declaratively by Spring Security's filter chain (see SecurityConfig), not by CatController
 * itself, so it isn't exercised by this standalone controller test. It's a security-filter
 * behavior best verified with a full Spring context / MockMvc + Spring Security test slice.
 */
class CatCriticApplicationTests {

    private MockMvc mockMvc;

    @Mock
    private CatService catService;

    @Mock
    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        CatController controller = new CatController(catService, ratingService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver =
                new org.springframework.web.servlet.view.InternalResourceViewResolver();

        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setViewResolvers(viewResolver)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(String username, Role role) {
        User user = new User();

        user.setId(1L);
        user.setUsername(username);
        user.setPasswordHash("irrelevant-already-hashed");
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                )
        );
    }

    @Test
    void testSubmitCatValidationErrors() throws Exception {

        loginAs("testuser", Role.CAT_VIEWER);

        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "",
                "image/png",
                new byte[0]
        );

        mockMvc.perform(multipart("/cats/new")
                        .file(photo)
                        .param("name", "")
                        .param("age", "-1")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("cat-form"))
                .andExpect(model().attributeHasFieldErrors("cat", "name"))
                .andExpect(model().attributeHasFieldErrors("cat", "age"))
                .andExpect(model().attributeHasFieldErrors("cat", "breed"))
                .andExpect(model().attributeHasFieldErrors("cat", "description"));
    }

    @Test
    void testSubmitCatSavesUnderAuthenticatedUsername() throws Exception {

        loginAs("testuser", Role.CAT_VIEWER);

        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "cat.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        Cat saved = new Cat();
        saved.setId(42L);

        Mockito.when(
                catService.saveCat(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq("testuser")
                )
        ).thenReturn(saved);

        mockMvc.perform(multipart("/cats/new")
                        .file(photo)
                        .param("name", "Whiskers")
                        .param("age", "3")
                        .param("breed", "SIAMESE")
                        .param("description", "A very good cat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cats/42"));
    }
}