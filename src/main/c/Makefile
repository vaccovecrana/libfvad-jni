CC = gcc
CFLAGS = -Wall -Wextra -O2 -fPIC
LDFLAGS = -shared
TARGET = libfvad.so

SRC_DIR = .
INCLUDE_DIR = include
INCLUDE_JNI = /home/jjzazuet/Applications/zulu17.42.21-ca-crac-jdk17.0.7-linux_x64/include
INCLUDE_LNX = /home/jjzazuet/Applications/zulu17.42.21-ca-crac-jdk17.0.7-linux_x64/include/linux

# Collect all source files in the src/ directory and its subdirectories
SRC_FILES = $(wildcard $(SRC_DIR)/*.c $(SRC_DIR)/*/*.c)
OBJ_FILES = $(SRC_FILES:.c=.o)

all: $(TARGET)

$(TARGET): $(OBJ_FILES)
	$(CC) $(LDFLAGS) -o $@ $(OBJ_FILES)

# Compile .c files to .o files
%.o: %.c
	$(CC) $(CFLAGS) -I$(INCLUDE_DIR) -I$(INCLUDE_JNI) -I$(INCLUDE_LNX) -c $< -o $@

clean:
	rm -f $(OBJ_FILES) $(TARGET)

.PHONY: all clean
