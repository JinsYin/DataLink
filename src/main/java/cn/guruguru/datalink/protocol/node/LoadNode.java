package cn.guruguru.datalink.protocol.node;

import cn.guruguru.datalink.datasource.NodeDataSource;
import cn.guruguru.datalink.datasource.DataSourceType;
import cn.guruguru.datalink.protocol.field.DataField;
import cn.guruguru.datalink.protocol.node.load.LakehouseLoadNode;
import cn.guruguru.datalink.protocol.relation.FieldRelation;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Data node for loading.
 *
 * @see org.apache.inlong.sort.protocol.node.LoadNode
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LakehouseLoadNode.class, name = LakehouseLoadNode.TYPE),
})
@NoArgsConstructor
@Data
public abstract class LoadNode implements Node, Serializable {
    @JsonProperty("id")
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("name")
    private String name;
    @JsonProperty("fields")
    private List<DataField> fields;
    @JsonProperty("fieldRelations")
    private List<FieldRelation> fieldRelations;
    /**
     * Filter clauses for Flink SQL, e.g. <pre>{@code WHERE age > 0 LIMIT 10}</pre>
     *
     * @deprecated set it in the extract node
     */
    @Nullable
    @JsonProperty("filterClause")
    @Deprecated
    private String filterClause;
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("properties")
    private Map<String, String> properties;

    @JsonCreator
    public LoadNode(@JsonProperty("id") String id,
                    @JsonProperty("name") String name,
                    @JsonProperty("fields") List<DataField> fields,
                    @JsonProperty("fieldRelations") List<FieldRelation> fieldRelations,
                    @Nullable @JsonProperty("filterClause") String filterClause,
                    @Nullable @JsonProperty("properties") Map<String, String> properties) {
        this.id = Preconditions.checkNotNull(id, "id is null");
        this.name = name;
        this.fields = Preconditions.checkNotNull(fields, "fields is null");
        Preconditions.checkState(!fields.isEmpty(), "fields is empty");
        this.fieldRelations = Preconditions.checkNotNull(fieldRelations,
                "fieldRelations is null");
        Preconditions.checkState(!fieldRelations.isEmpty(), "fieldRelations is empty");
        // TODO: check syntax
        this.filterClause = filterClause;
        this.properties = properties;
    }

    DataSourceType getDataSourceType() {
        return this.getClass().getAnnotation(NodeDataSource.class).value();
    }
}
