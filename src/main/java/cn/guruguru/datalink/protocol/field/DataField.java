package cn.guruguru.datalink.protocol.field;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.annotation.Nullable;

/**
 * Data Field
 *
 * @see org.apache.inlong.sort.protocol.FieldInfo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DataField.class, name = DataField.TYPE), // InLong Sort: field
        @JsonSubTypes.Type(value = MetaField.class, name = MetaField.TYPE),
})
@Data
public class DataField implements Field {
    public static final String TYPE = "DataField";
    private static final long serialVersionUID = 5871970550803344673L;

    @JsonProperty("name")
    private final String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("nodeId")
    private String nodeId;
    @Nullable
    @JsonProperty("comment")
    private String comment;
    /**
     * It will be null if the field is a meta field
     *
     * @see org.apache.inlong.sort.formats.common.FormatInfo
     * @see <a href="https://stackoverflow.com/questions/19564711/json-jackson-parse-different-keys-into-same-field">
     *     JSON Jackson parse different keys into same field</a>
     */
    @Nullable
    @JsonAlias({"dataType", "fieldFormat"}) // `fieldFormat` is old
    private DataType dataType;

    public DataField(
            @JsonProperty("name") String name,
            @JsonProperty("dataType") DataType dataType) {
        this(name, null, null, dataType);
    }

    public DataField(@JsonProperty("name") String name) {
        this(name, null, null, null);
    }

    @JsonCreator
    public DataField(
            @JsonProperty("name") String name,
            @JsonProperty("nodeId") String nodeId,
            @Nullable @JsonProperty("comment") String comment,
            @Nullable @JsonProperty("dataType") DataType dataType) {
        this.name = Preconditions.checkNotNull(name);
        this.nodeId = nodeId;
        this.comment = comment;
        this.dataType = dataType;
    }

    public String format() {
        String formatName = name.trim();
        if (!formatName.contains(".")) {
            if (!formatName.startsWith("`")) {
                formatName = String.format("`%s", formatName);
            }
            if (!formatName.endsWith("`")) {
                formatName = String.format("%s`", formatName);
            }
        }
        return formatName;
    }
}
