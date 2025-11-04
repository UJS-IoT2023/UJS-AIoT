#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <pthread.h>

#define PORT 5678
#define BUFFER_SIZE 1024

void* handle_client(void* arg) {
    int client_fd = *(int*)arg;
    free(arg);

    char buffer[BUFFER_SIZE];
    FILE* stream = fdopen(client_fd, "r+");
    if (!stream) {
        perror("fdopen");
        close(client_fd);
        return NULL;
    }

    char client_ip[INET_ADDRSTRLEN];
    struct sockaddr_in client_addr;
    socklen_t client_len = sizeof(client_addr);
    getpeername(client_fd, (struct sockaddr*)&client_addr, &client_len);
    inet_ntop(AF_INET, &client_addr.sin_addr, client_ip, INET_ADDRSTRLEN);

    printf("新客户端连接: %s\n", client_ip);

    char* line = NULL;
    size_t len = 0;

    while (1) {
        ssize_t n = getline(&line, &len, stream);
        if (n <= 0) break;

        if (line[n-1] == '\n') line[n-1] = '\0';

        printf("收到 [%s]: %s\n", client_ip, line);

        fprintf(stream, "Server 收到: %s\n", line);
        fflush(stream);
    }

    free(line);
    printf("客户端 %s 断开\n", client_ip);
    fclose(stream);
    return NULL;
}

int main() {
    int server_fd, client_fd;
    struct sockaddr_in server_addr, client_addr;
    socklen_t client_len = sizeof(client_addr);

    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) {
        perror("socket");
        exit(1);
    }

    int opt = 1;
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("bind");
        close(server_fd);
        exit(1);
    }

    if (listen(server_fd, 10) < 0) {
        perror("listen");
        close(server_fd);
        exit(1);
    }

    printf("TCP Server 正在监听端口 %d\n", PORT);

    while (1) {
        client_fd = accept(server_fd, (struct sockaddr*)&client_addr, &client_len);
        if (client_fd < 0) {
            perror("accept");
            continue;
        }

        pthread_t tid;
        int* pclient = malloc(sizeof(int));
        *pclient = client_fd;
        pthread_create(&tid, NULL, handle_client, pclient);
        pthread_detach(tid);
    }

    close(server_fd);
    return 0;
}
