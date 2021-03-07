package template.infra.persistence;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import template.base.stereotype.Entity;

@Getter
@AllArgsConstructor
public final class UserEntity implements Entity {

  public static final String C_TABLE = "user";
  public static final String C_NAME = "name";
  public static final String C_AGE = "age";

  private final @NonNull String id;
  private final @NonNull String name;
  private final @NonNull Integer age;

  @Override
  public String getId() {
    return id;
  }

  public interface Mapper extends template.base.contract.Mapper<UserEntity> {

    @Override
    @Results(id = C_TABLE, value = {
        @Result(column = C_ID, property = C_ID, jdbcType = JdbcType.VARCHAR,
            id = true),
        @Result(column = C_NAME, property = C_NAME, jdbcType =
            JdbcType.VARCHAR),
        @Result(column = C_AGE, property = C_AGE, jdbcType = JdbcType.NUMERIC),
    })
    Set<UserEntity> getMany(final @NonNull SelectStatementProvider stmt);

    @Override
    @ResultMap(C_TABLE)
    UserEntity getOne(final @NonNull SelectStatementProvider stmt);
  }

  public static final class Table extends SqlTable {

    // public final @NonNull SqlColumn<String> id = column(C_ID);
    public final @NonNull SqlColumn<String> name = column(C_NAME);
    public final @NonNull SqlColumn<Integer> age = column(C_AGE);

    public Table() {
      super(C_TABLE);
    }
  }
}
