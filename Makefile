all: build run

build:
	mvn clean install

run:
	mvn exec:java

clean:
	mvn clean

.PHONY: all clean