package com.kaleido.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.kaleido.web.rest.TestUtil;

public class PlateMapTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlateMap.class);
        PlateMap plateMap1 = new PlateMap();
        plateMap1.setId(1L);
        PlateMap plateMap2 = new PlateMap();
        plateMap2.setId(plateMap1.getId());
        assertThat(plateMap1).isEqualTo(plateMap2);
        plateMap2.setId(2L);
        assertThat(plateMap1).isNotEqualTo(plateMap2);
        plateMap1.setId(null);
        assertThat(plateMap1).isNotEqualTo(plateMap2);
    }
}
