<#--noinspection ALL-->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.wladischlau.vlt.route</groupId>
    <artifactId>route</artifactId>
    <version>${versions.vlt}</version>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>com.wladischlau.vlt</groupId>
            <artifactId>vlt-adapters</artifactId>
            <version>${versions.vlt}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>${versions.spring_boot}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>${versions.spring_boot}</version>
        </dependency>

        <dependency>
            <groupId>me.paulschwarz</groupId>
            <artifactId>spring-dotenv</artifactId>
            <version>${versions.spring_dotenv}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${versions.lombok}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>${repo.id}</id>
            <name>${repo.name}</name>
            <url>${repo.url}</url>
        </repository>
    </repositories>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${versions.maven_compiler_plugin}</version>
                <configuration>
                    <release>${versions.java}</release>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${versions.lombok}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${versions.spring_boot}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>build-image</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <image>
                        <publish>true</publish>
                        <name>${docker.image_registry}:${route.uuid}.${route.commit_hash}</name>
                        <env>
                            <BP_JVM_VERSION>${versions.java}</BP_JVM_VERSION>
                            <#-- Конфигурация для buildpack по-умолчанию -->
                            <BPE_DELIM_JAVA_TOOL_OPTIONS xml:space="preserve"> </BPE_DELIM_JAVA_TOOL_OPTIONS>
                            <BPE_APPEND_JAVA_TOOL_OPTIONS>-XX:+HeapDumpOnOutOfMemoryError</BPE_APPEND_JAVA_TOOL_OPTIONS>
                        </env>
                        <builder>${route.image.builder}</builder>
                        <buildpacks>
                            <#if route.image.buildpacks?? && route.image.buildpacks?size gt 0>
                                <#list route.image.buildpacks?keys as key>
                                    <buildpack>${route.image.buildpacks[key]}</buildpack>
                                </#list>
                            </#if>
                        </buildpacks>
                    </image>
                    <docker>
                        <publishRegistry>
                            <username>${docker.username}</username>
                            <password>${docker.password}</password>
                        </publishRegistry>
                    </docker>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>