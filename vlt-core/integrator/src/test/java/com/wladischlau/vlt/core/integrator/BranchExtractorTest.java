package com.wladischlau.vlt.core.integrator;

import com.wladischlau.vlt.core.integrator.model.Branch;
import com.wladischlau.vlt.core.integrator.model.Node;
import com.wladischlau.vlt.core.integrator.model.RouteDefinition;
import com.wladischlau.vlt.core.integrator.service.BranchExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchExtractorTest {

    private final BranchExtractor extractor = new BranchExtractor();

    @Test
    void extractBranches_singleNode() {
        RouteDefinition rd = mock(RouteDefinition.class);
        Node single = mock(Node.class);
        when(rd.getNodes()).thenReturn(Map.of(UUID.randomUUID(), single));

        List<Branch> branches = extractor.extractBranches(rd);

        assertThat(branches).hasSize(1);
        Branch b = branches.getFirst();
        assertThat(b.getNodes()).containsExactly(single);
    }
}