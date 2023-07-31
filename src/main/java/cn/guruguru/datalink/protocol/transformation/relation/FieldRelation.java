package cn.guruguru.datalink.protocol.transformation.relation;

import cn.guruguru.datalink.protocol.DataField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;

/**
 * Field Relation
 *
 * @see org.apache.inlong.sort.protocol.transformation.FieldRelation
 */
@JsonTypeName("fieldRelation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@Data
@NoArgsConstructor
public class FieldRelation implements Serializable {
    /**
     * Input field
     *
     * @see org.apache.inlong.sort.protocol.transformation.FunctionParam
     */
    @JsonProperty("inputField")
    private DataField inputField;
    @JsonProperty("outputField")
    private DataField outputField;

    @JsonCreator
    public FieldRelation(@JsonProperty("inputField") DataField inputField,
                         @JsonProperty("outputField") DataField outputField) {
        this.inputField = inputField;
        this.outputField = outputField;
    }
}
