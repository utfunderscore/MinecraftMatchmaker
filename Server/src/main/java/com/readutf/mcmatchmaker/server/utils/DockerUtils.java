package com.readutf.mcmatchmaker.server.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class DockerUtils {

    public static Integer getDockerPort() {

        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();

        // Create Docker client
        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config).build();

        // List containers
        List<com.github.dockerjava.api.model.Container> containers = dockerClient.listContainersCmd().exec();


        String finalHostName = hostName;
        Container container = containers.stream().filter(container1 -> container1.getId().startsWith(finalHostName)).findFirst().orElse(null);

        if(container == null) {
            throw new RuntimeException("This shouldn't happen.");
        }

        for (ContainerPort port : container.getPorts()) {
            return port.getPublicPort();
        }


        return null;
    }


    public static class DockerAddress {

        public DockerAddress(String address, int port) {
            this.address = address;
            this.port = port;
        }

        private final String address;
        private final int port;


    }

}
