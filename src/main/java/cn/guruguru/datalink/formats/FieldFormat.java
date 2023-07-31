package cn.guruguru.datalink.formats;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
public class FieldFormat {
    @JsonProperty("field")
    private String field;
    @JsonProperty("type")
    private String type;
    @JsonProperty("precision")
    private int precision;
    @JsonProperty("scale")
    private int scale;

    @JsonCreator
    public FieldFormat(@JsonProperty("field") String field,
                         @JsonProperty("type") String type,
                         @JsonProperty("precision") int precision,
                         @JsonProperty("scale") int scale) {
        this.field = field;
        this.type = type;
        this.precision = precision;
        this.scale = scale;
    }
}
