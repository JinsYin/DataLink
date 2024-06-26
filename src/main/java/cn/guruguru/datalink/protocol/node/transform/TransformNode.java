package cn.guruguru.datalink.protocol.node.transform;

import cn.guruguru.datalink.protocol.field.DataField;
import cn.guruguru.datalink.protocol.node.Node;
import cn.guruguru.datalink.protocol.relation.FieldRelation;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Data node for transforming.
 *
 * @see org.apache.inlong.sort.protocol.node.transform.TransformNode
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransformNode.class, name = TransformNode.TYPE), // InLong Sort: baseTransform
})
@Data
@NoArgsConstructor
public class TransformNode implements Node, Serializable {
    private static final long serialVersionUID = -1202158328274891592L;
    public static final String TYPE = "Transform";

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("fields")
    private List<DataField> fields;
    @JsonProperty("fieldRelations")
    private List<FieldRelation> fieldRelations;
    /**
     * Filter clauses for Flink SQL e.g. <pre>{@code WHERE age > 0 LIMIT 10}</pre>
     */
    @Nullable
    @JsonProperty("filterClause")
    private String filterClause;

    @JsonCreator
    public TransformNode(@JsonProperty("id") String id,
                         @JsonProperty("name") String name,
                         @JsonProperty("fields") List<DataField> fields,
                         @JsonProperty("fieldRelations") List<FieldRelation> fieldRelations,
                         @Nullable @JsonProperty("filterClause") String filterClause) {
        this.id = Preconditions.checkNotNull(id, "id is null");
        this.name = name;
        this.fields = Preconditions.checkNotNull(fields, "fields is null");
        Preconditions.checkState(!fields.isEmpty(), "fields is empty");
        this.fieldRelations = Preconditions.checkNotNull(fieldRelations,
                "fieldRelations is null");
        Preconditions.checkState(!fieldRelations.isEmpty(), "fieldRelations is empty");
        this.filterClause = filterClause;
    }

    @JsonIgnore
    @Override
    public Map<String, String> getProperties() {
        return null;
    }

    @Override
    public String genTableName() {
        return quoteIdentifier("transform_" + id); // InLong Sort: "tansform_" + id
    }
}
