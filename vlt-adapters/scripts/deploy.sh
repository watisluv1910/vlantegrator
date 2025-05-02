#!/bin/bash

export $(grep -v '^#' .env | xargs)
./mvnw clean compile deploy