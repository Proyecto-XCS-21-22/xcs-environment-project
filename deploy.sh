#!/bin/sh
mvn clean install
cd web
mvn wildfly:deploy
