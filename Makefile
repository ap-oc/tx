#!make

.PHONY build

build:
	lein compile && lein uberjar