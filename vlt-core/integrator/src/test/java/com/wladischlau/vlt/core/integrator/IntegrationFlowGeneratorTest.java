package com.wladischlau.vlt.core.integrator;

import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.model.Node;
import com.wladischlau.vlt.core.integrator.service.BranchExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class IntegrationFlowGeneratorTest {

    @Autowired
    private IntegrationFlowGenerator generator;

    @MockitoBean
    private BranchExtractor branchExtractor;

    @Test
    void getClassFromFullyQualifiedName_validClassName_returnClass() throws Exception {
        Method m = IntegrationFlowGenerator.class
                .getDeclaredMethod("getClassFromFullyQualifiedName", String.class);
        m.setAccessible(true);

        Object result = m.invoke(generator, "java.lang.String");
        assertThat(result).isEqualTo(String.class);
    }

    @Test
    void getClassFromFullyQualifiedName_invalidClassNae_throwRuntimeException() throws Exception {
        Method m = IntegrationFlowGenerator.class
                .getDeclaredMethod("getClassFromFullyQualifiedName", String.class);
        m.setAccessible(true);

        Throwable thrown = catchThrowable(() -> m.invoke(generator, "no.such.Class"));
        assertThat(thrown)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(RuntimeException.class)
                .cause()
                .hasMessageContaining("Unable to find class");
    }

    @Test
    void generateFlowConfig_withNoBranches_writeEmptyConfigClass(@TempDir Path tempDir) throws Exception {
        given(branchExtractor.extractBranches(any())).willReturn(List.of());

        generator.generateFlowConfig(null, tempDir);

        Path javaFile = tempDir
                .resolve("com")
                .resolve("wladischlau")
                .resolve("vlt")
                .resolve("route")
                .resolve("IntegrationFlowConfig.java");

        assertThat(javaFile).exists();
        String contents = Files.readString(javaFile);

        assertThat(contents)
                .contains("package com.wladischlau.vlt.route;")
                .contains("public class IntegrationFlowConfig");
    }


    @Test
    void generateUniqueChannelName_prefixesAdapterName() throws Exception {
        Node fakeNode = mock(Node.class);
        var fakeAdapter = mock(Node.class.getMethod("adapter").getReturnType());
        given(fakeNode.adapter()).willReturn((Adapter) fakeAdapter);
        given(((Adapter) fakeAdapter).name()).willReturn("MyAdapter");

        Method m = IntegrationFlowGenerator.class.getDeclaredMethod("generateUniqueChannelName", Node.class);
        m.setAccessible(true);

        String channel = (String) m.invoke(generator, fakeNode);
        assertThat(channel)
                .startsWith("MyAdapter_")
                .matches("MyAdapter_[0-9a-fA-F\\-]{36}");
    }
}
