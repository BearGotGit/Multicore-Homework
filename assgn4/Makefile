.PHONY: compile run clean

compile:
	javac -d out/ src/hw/*.java

run: compile
	java -cp out hw.Main

clean:
	rm -rf out/