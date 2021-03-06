package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ImageBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

@SpringBootApplication
public class DemoApplication {

    @Bean
    DefaultFtpSessionFactory ftpSessionFactory(@Value("${ftp.prot:21}") int port,
                                               @Value("${ftp.username:lpz}") String username,
                                               @Value("${ftp.password:lpz}") String pw) {
        DefaultFtpSessionFactory ftpSessionFactory = new DefaultFtpSessionFactory();
        ftpSessionFactory.setPort(port);
        ftpSessionFactory.setUsername(username);
        ftpSessionFactory.setPassword(pw);
        ftpSessionFactory.setHost("192.168.2.216");
        return ftpSessionFactory;
    }

    @Bean
        // IntegrationFlow files(@Value("${input-directory:c:\\Desktop\\in") File in,
    IntegrationFlow files(@Value("${input-directory:c:\\Desktop\\in}") File in,
                          Environment environment,
                          DefaultFtpSessionFactory ftpSessionFactory) {
        GenericTransformer<File, Message<String>> fileStringGenericTransformer = (File source) -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 PrintStream printStream = new PrintStream(baos)) {
                ImageBanner imageBanner = new ImageBanner(new FileSystemResource(source));
                imageBanner.printBanner(environment, getClass(), printStream);
                return MessageBuilder.withPayload(new String(baos.toByteArray()))
                        .setHeader(FileHeaders.FILENAME, source.getAbsoluteFile().getName())
                        .build();
            } catch (IOException e) {
                ReflectionUtils.rethrowRuntimeException(e);
            }
            return null;
        };
        return IntegrationFlows
                .from(Files.inboundAdapter(in).autoCreateDirectory(true).preventDuplicates().patternFilter("*.jpg"),
                        poller -> poller.poller(pm -> pm.fixedRate(1000)))

                .transform(File.class, fileStringGenericTransformer)
                .handleWithAdapter(adapters -> adapters.ftp(ftpSessionFactory)
                        .remoteDirectory("uploads")
                        .fileNameGenerator(message -> {
                            Object o = message.getHeaders().get(FileHeaders.FILENAME);
                            String fileName = String.class.cast(o);
                            return fileName.split("\\.")[0] + ".txt";
                        })
                ).get();
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
