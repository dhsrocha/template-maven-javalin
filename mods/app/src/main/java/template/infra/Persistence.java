package template.infra;

import dagger.Module;
import dagger.Provides;
import lombok.NonNull;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import template.Application;

@Module
public interface Persistence {

  @Provides
  static SqlSessionFactory sessionFactory(final @NonNull Application.Mode mode) {
    final var ds = new PooledDataSource("",
                                        "",
                                        "",
                                        "");
    final var trans = new JdbcTransactionFactory();
    final var cfg = new Configuration(new Environment(mode.name(), trans, ds));
    return new SqlSessionFactoryBuilder().build(cfg);
  }

  @Provides
  static SqlSession session(final @NonNull SqlSessionFactory factory){
    try (final var s = factory.openSession()) {
      return s;
    }
  }
}
