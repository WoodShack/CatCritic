package com.cpan228.catcritic;

import com.cpan228.catcritic.controller.AuthController;
import com.cpan228.catcritic.controller.CatController;
import com.cpan228.catcritic.service.CatService;
import com.cpan228.catcritic.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .build();
    }

    @Test
    void testSubmitCatValidationErrors() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthController.SESSION_USERNAME, "testuser");

        MockMultipartFile photo = new MockMultipartFile("photo", "", "image/png", new byte[0]);

        mockMvc.perform(multipart("/cats/new")
                        .file(photo)
                        .session(session)
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
    void testSubmitCatRequiresLogin() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "cat.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/cats/new")
                        .file(photo)
                        .param("name", "Whiskers")
                        .param("age", "3")
                        .param("breed", "SIAMESE")
                        .param("description", "A very good cat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
