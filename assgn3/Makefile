# Makefile for Tournament Consensus

SRC_DIR = src
BIN_DIR = bin

compile:
	javac -d $(BIN_DIR) $(SRC_DIR)/*.java

run: compile
	java -ea -cp $(BIN_DIR) TestConsensus

clean:
	rm -rf $(BIN_DIR)/*.class
