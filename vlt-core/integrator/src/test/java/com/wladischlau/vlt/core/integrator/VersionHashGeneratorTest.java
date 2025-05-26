package com.wladischlau.vlt.core.integrator;

import com.wladischlau.vlt.core.integrator.config.properties.VltProperties;
import com.wladischlau.vlt.core.integrator.utils.VersionHashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionHashGeneratorTest {

    @Test
    void generate_lengthMatchesProperty() {
        VltProperties props = mock(VltProperties.class);
        when(props.getVersionHashLength()).thenReturn(7);
        VersionHashGenerator gen = new VersionHashGenerator(props);
        String hash = gen.generate();
        assertThat(hash).hasSize(7);
    }

    @Test
    void generate_zeroLengthReturnsEmptyString() {
        VltProperties props = mock(VltProperties.class);
        when(props.getVersionHashLength()).thenReturn(0);
        VersionHashGenerator gen = new VersionHashGenerator(props);
        String hash = gen.generate();
        assertThat(hash).isEmpty();
    }

    @Test
    void generate_containsOnlyHexCharacters() {
        VltProperties props = mock(VltProperties.class);
        when(props.getVersionHashLength()).thenReturn(12);
        VersionHashGenerator gen = new VersionHashGenerator(props);
        String hash = gen.generate();
        assertThat(hash).matches("^[0-9a-f]{12}$");
    }

    @Test
    void generate_oddLengthTruncationCorrect() {
        VltProperties props = mock(VltProperties.class);
        when(props.getVersionHashLength()).thenReturn(3);
        VersionHashGenerator gen = new VersionHashGenerator(props);
        String hash = gen.generate();
        assertThat(hash).hasSize(3);
        assertThat(hash).matches("^[0-9a-f]{3}$");
    }

    @Test
    void generate_multipleGenerationsVary() {
        VltProperties props = mock(VltProperties.class);
        when(props.getVersionHashLength()).thenReturn(8);
        VersionHashGenerator gen = new VersionHashGenerator(props);
        Set<String> hashes = new HashSet<>();
        IntStream.range(0, 10).forEach(i -> hashes.add(gen.generate()));
        assertThat(hashes.size()).isGreaterThan(1);
    }
}
