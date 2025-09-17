# Minimal Makefile
SRC_DIR := src
BUILD_DIR := build
TARGET := $(BUILD_DIR)/treelock_demo

CC := gcc
CFLAGS := -std=c11 -O2 -pthread
SRCS := $(wildcard $(SRC_DIR)/*.c)

all: $(TARGET)

$(TARGET): $(SRCS)
	@mkdir -p $(BUILD_DIR)
	$(CC) $(CFLAGS) $(SRCS) -o $@

run: $(TARGET)
	./$(TARGET)

clean:
	rm -f $(TARGET)

.PHONY: all run clean