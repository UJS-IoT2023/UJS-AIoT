#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>

#define SERVER_IP "127.0.0.1" // Localhost
#define PORT 5678
#define BUFFER_SIZE 1024

int main() {
    int client_fd;
    struct sockaddr_in server_addr;
    char *message = "Hello from client!";
    char buffer[BUFFER_SIZE] = {0};
    
    // Create socket
    client_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (client_fd < 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }
    
    // Configure server address
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    
    // Convert IP address from string to binary
    if (inet_pton(AF_INET, SERVER_IP, &server_addr.sin_addr) <= 0) {
        perror("Invalid address or address not supported");
        close(client_fd);
        exit(EXIT_FAILURE);
    }
    
    // Connect to server
    if (connect(client_fd, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("Connection failed");
        close(client_fd);
        exit(EXIT_FAILURE);
    }
    
    printf("Connected to server %s:%d\n", SERVER_IP, PORT);
    
    // Send data packet
    if (send(client_fd, message, strlen(message), 0) < 0) {
        perror("Send failed");
        close(client_fd);
        exit(EXIT_FAILURE);
    }
    
    printf("Sent data: %s\n", message);
    
    // Optional: Receive response from server (if expected)
    int bytes_received = recv(client_fd, buffer, BUFFER_SIZE - 1, 0);
    if (bytes_received < 0) {
        perror("Receive failed");
    } else if (bytes_received > 0) {
        buffer[bytes_received] = '\0';
        printf("Received response: %s\n", buffer);
    }
    
    // Cleanup
    close(client_fd);
    printf("Connection closed\n");
    return 0;
}