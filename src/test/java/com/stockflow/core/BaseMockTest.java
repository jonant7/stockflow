package com.stockflow.core;

import com.stockflow.core.config.MessageResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BaseMockTest {

    @BeforeAll
    static void initMessageResolver() {
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(anyString(), any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        new MessageResolver(messageSource);
    }


    @SuppressWarnings("unchecked")
    protected static <T> Specification<T> anySpecification() {
        return (Specification<T>) any(Specification.class);
    }

}
