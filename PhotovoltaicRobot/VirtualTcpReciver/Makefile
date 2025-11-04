# 编译器
CC := gcc

# 编译选项
CFLAGS := -Wall -Wextra -g

# 目标目录
BUILD_DIR := build

# 源文件
CLIENT_SRC := tcp_client.c
SERVER_SRC := tcp_server.c

# 目标可执行文件
CLIENT_TARGET := $(BUILD_DIR)/tcp_client
SERVER_TARGET := $(BUILD_DIR)/tcp_server

# 中间目标文件
CLIENT_OBJ := $(BUILD_DIR)/tcp_client.o
SERVER_OBJ := $(BUILD_DIR)/tcp_server.o

# 默认目标
all: $(CLIENT_TARGET) $(SERVER_TARGET)

# 链接客户端
$(CLIENT_TARGET): $(CLIENT_OBJ) | $(BUILD_DIR)
	$(CC) $(CFLAGS) -o $@ $<

# 链接服务端
$(SERVER_TARGET): $(SERVER_OBJ) | $(BUILD_DIR)
	$(CC) $(CFLAGS) -o $@ $<

# 编译客户端 .o 文件
$(CLIENT_OBJ): $(CLIENT_SRC) | $(BUILD_DIR)
	$(CC) $(CFLAGS) -c -o $@ $<

# 编译服务端 .o 文件
$(SERVER_OBJ): $(SERVER_SRC) | $(BUILD_DIR)
	$(CC) $(CFLAGS) -c -o $@ $<

# 确保 build 目录存在
$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)

# 清理
clean:
	rm -rf $(BUILD_DIR)

# 声明伪目标
.PHONY: all clean