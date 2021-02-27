package template;

import com.github.dockerjava.api.model.ContainerSpec;
import com.github.dockerjava.api.model.EndpointSpec;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.api.model.MountType;
import com.github.dockerjava.api.model.NetworkAttachmentConfig;
import com.github.dockerjava.api.model.PortConfig;
import com.github.dockerjava.api.model.PortConfig.PublishMode;
import com.github.dockerjava.api.model.ServiceGlobalModeOptions;
import com.github.dockerjava.api.model.ServiceModeConfig;
import com.github.dockerjava.api.model.ServiceReplicatedModeOptions;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.TaskSpec;
import com.github.dockerjava.api.model.UpdateConfig;
import com.github.dockerjava.api.model.UpdateFailureAction;
import com.google.common.collect.ImmutableList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.val;

@AllArgsConstructor
enum Middleware {
  RDB("postgres",
      ImmutableList.of(),
      ServiceMode.GLOBAL,
      ImmutableList.of(Mounts.SOCKET, Mounts.RDB),
      ImmutableList.of(Port.RDB),
      ImmutableList.of(Network.RDB),
      ImmutableList.of("POSTGRES_DB=" + Credentials.DB_NAME,
                       "POSTGRES_USER=" + Credentials.DB_USER,
                       "POSTGRES_PASSWORD=" + Credentials.DB_PASS),
      ImmutableList.of()),
  RDB_CLIENT("adminer",
             ImmutableList.of(RDB),
             ServiceMode.GLOBAL,
             ImmutableList.of(Mounts.SOCKET),
             ImmutableList.of(Port.RDB_CLIENT),
             ImmutableList.of(Network.RDB),
             ImmutableList.of("ADMINER_DEFAULT_SERVER=" + RDB),
             ImmutableList.of()),
  NDB("mongo",
      ImmutableList.of(),
      ServiceMode.GLOBAL,
      ImmutableList.of(Mounts.SOCKET, Mounts.NDB, Mounts.NDB_CFG),
      ImmutableList.of(Port.NDB),
      ImmutableList.of(Network.NDB),
      ImmutableList.of("MONGO_INITDB_ROOT_USERNAME=" + Credentials.DB_USER,
                       "MONGO_INITDB_ROOT_PASSWORD=" + Credentials.DB_PASS),
      ImmutableList.of()),
  NDB_CLIENT("mongo-express",
             ImmutableList.of(NDB),
             ServiceMode.GLOBAL,
             ImmutableList.of(Mounts.SOCKET),
             ImmutableList.of(Port.NDB_CLIENT),
             ImmutableList.of(Network.NDB),
             ImmutableList.of("ME_CONFIG_MONGODB_ENABLE_ADMIN=true",
                              "ME_CONFIG_OPTIONS_EDITORTHEME=rubyblue",
                              "ME_CONFIG_MONGODB_SERVER=" + Middleware.NDB,
                              "ME_CONFIG_MONGODB_PORT=" + Port.NDB.exposed,
                              "ME_CONFIG_MONGODB_AUTH_DATABASE=" + Credentials.DB_NAME,
                              "ME_CONFIG_MONGODB_AUTH_USERNAME=" + Credentials.DB_USER,
                              "ME_CONFIG_MONGODB_AUTH_PASSWORD=" + Credentials.DB_PASS,
                              "ME_CONFIG_MONGODB_ADMINUSERNAME=" + Credentials.DB_USER,
                              "ME_CONFIG_MONGODB_ADMINPASSWORD=" + Credentials.DB_PASS),
             ImmutableList.of()),
  MSG("rabbitmq:management",
      ImmutableList.of(),
      ServiceMode.GLOBAL,
      ImmutableList.of(Mounts.SOCKET, Mounts.MSG),
      ImmutableList.of(Port.MSG, Port.MSG_CLIENT),
      ImmutableList.of(Network.MSG),
      ImmutableList.of("RABBITMQ_DEFAULT_USER=" + Credentials.DB_USER,
                       "RABBITMQ_DEFAULT_PASS=" + Credentials.DB_PASS),
      ImmutableList.of()),
  AGENT("portainer/portainer-ce",
        ImmutableList.of(),
        ServiceMode.GLOBAL,
        ImmutableList.of(Mounts.SOCKET, Mounts.AGENT),
        ImmutableList.of(Port.AGENT),
        ImmutableList.of(),
        ImmutableList.of(),
        ImmutableList.of()),
  SCRAPPER("prom/prometheus",
           ImmutableList.of(),
           ServiceMode.REPLICA,
           ImmutableList.of(),
           ImmutableList.of(Port.SCRAPPER),
           ImmutableList.of(Network.SCRAPPER),
           ImmutableList.of(),
           ImmutableList.of()),
  SCRAPPER_RDB("prom/mysqld-exporter",
               ImmutableList.of(SCRAPPER, RDB),
               ServiceMode.GLOBAL,
               ImmutableList.of(),
               ImmutableList.of(Port.SCRAPPER_RDB),
               ImmutableList.of(Network.RDB, Network.SCRAPPER),
               ImmutableList.of("DATA_SOURCE_NAME="
                                    + Credentials.DB_USER + ":" + Credentials.DB_PASS
                                    + "@(" + RDB + ":" + Port.RDB.published + ")"),
               ImmutableList.of("/bin/mysqld_exporter",
                                "--collect.info_schema.processlist",
                                "--collect.info_schema.innodb_metrics",
                                "--collect.info_schema.tablestats",
                                "--collect.info_schema.tables",
                                "--collect.info_schema.userstats",
                                "--collect.engine_innodb_status",
                                "--web.listen-address=:" + Port.RDB.published)),
  //  MONITOR("grafana/grafana",
  //          ImmutableList.of(SCRAPPER),
  //          ServiceMode.GLOBAL,
  //          ImmutableList.of(),
  //          ImmutableList.of(Port.MONITOR),
  //          ImmutableList.of(Network.MONITOR),
  //          ImmutableList.of("GF_SECURITY_ADMIN_USER=" + Credentials.DB_USER,
  //                           "GF_SECURITY_ADMIN_PASSWORD=" + Credentials
  //                           .DB_PASS),
  //          ImmutableList.of()),
  ;

  private final String image;
  private final ImmutableList<Middleware> dependOn;
  private final ServiceMode mode;
  private final ImmutableList<Mounts> mounts;
  private final ImmutableList<Port> ports;
  private final ImmutableList<Network> refs;
  private final ImmutableList<String> entries;
  private final ImmutableList<String> commands;

  static Stream<Middleware> stream(final String ss) {
    return Stream.of(ss.split(",")).map(Middleware::valueOf).distinct();
  }

  ServiceSpec spec() {
    val pp = ports.stream().map(Supplier::get).collect(Collectors.toList());
    val mm = mounts.stream().map(Supplier::get).collect(Collectors.toList());
    val u = new UpdateConfig()
        .withFailureAction(UpdateFailureAction.ROLLBACK);
    val n = refs.stream().map(Enum::name)
                .map(new NetworkAttachmentConfig()::withTarget)
                .collect(Collectors.toList());
    val c = new ContainerSpec().withMounts(mm).withImage(image)
                               .withEnv(entries).withCommand(commands);
    val t = new TaskSpec().withContainerSpec(c);
    val e = new EndpointSpec().withPorts(pp);
    return new ServiceSpec().withName(name()).withTaskTemplate(t)
                            .withNetworks(n).withEndpointSpec(e)
                            .withMode(mode.mode()).withRollbackConfig(u);
  }

  final ImmutableList<Middleware> dependOn() {
    return dependOn;
  }

  @AllArgsConstructor
  private enum Port implements Supplier<PortConfig> {
    RDB(9011, 5432),
    NDB(9012, 27017),
    MSG(9013, 5672),
    // Clients
    AGENT(9000, 9000),
    MONITOR(9001, 3000),
    RDB_CLIENT(9003, 8080),
    NDB_CLIENT(9004, 8081),
    MSG_CLIENT(9005, 15672),
    // Monitoring
    SCRAPPER(9021, 9090),
    SCRAPPER_RDB(9022, 9104),
    ;
    private final int published;
    private final int exposed;

    @Override
    public PortConfig get() {
      return new PortConfig().withTargetPort(exposed)
                             .withPublishedPort(published)
                             .withPublishMode(PublishMode.host);
    }
  }

  private enum Network {
    RDB, NDB, MSG, SCRAPPER
  }

  @AllArgsConstructor
  private enum Mounts implements Supplier<Mount> {
    MSG("/var/lib/rabbitmq"),
    RDB("/var/lib/postgresql/data"),
    NDB("/data/db"),
    NDB_CFG("/data/configdb"),
    AGENT("/data"),
    SOCKET("") {
      @Override
      public Mount get() {
        return new Mount().withSource(Constants.SOCKET)
                          .withTarget(Constants.SOCKET)
                          .withReadOnly(Boolean.TRUE);
      }
    },
    ;
    private final String val;

    @Override
    public Mount get() {
      return new Mount().withType(MountType.VOLUME)
                        .withSource(name()).withTarget(val);
    }
  }

  @AllArgsConstructor
  private enum ServiceMode {
    GLOBAL(0),
    REPLICA(1),
    ;
    private final int scale;

    ServiceModeConfig mode() {
      return scale == 0
          ? new ServiceModeConfig().withGlobal(new ServiceGlobalModeOptions())
          : new ServiceModeConfig().withReplicated(
          new ServiceReplicatedModeOptions().withReplicas(scale));
    }
  }

  @UtilityClass
  static class Constants {
    static final String SOCKET = "/var/run/docker.sock";
  }

  @UtilityClass
  private static class Credentials {
    private static final String DB_NAME = "base";
    private static final String DB_USER = "user";
    private static final String DB_PASS = "pass";
  }
}
