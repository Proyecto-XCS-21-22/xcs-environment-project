#!/bin/sh
mvn install
cd web
mvn wildfly:deploy
