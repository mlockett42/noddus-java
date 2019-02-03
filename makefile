JFLAGS = -g -cp '.:protobuf_build/:lib/json-20180813.jar:lib/protobuf-java-3.6.1.jar'
JC = javac
PC = protoc
SRC_DIR = /opt/project
DST_DIR = /opt/project/protobuf_build/
PCFLAGS = -I=$(SRC_DIR) --java_out=$(DST_DIR)
.SUFFIXES: .java .class .proto
.java.class:
	$(JC) $(JFLAGS) $*.java

.proto.java:
	$(PC) $(PCFLAGS) $(SRC_DIR)/$*.proto

CLASSES = \
	webserver.java

PROTOBUFS = \
	noddus.proto

default: compile

protocompile: $(PROTOBUFS:.proto=.java)

javacompile: $(CLASSES:.java=.class)

compile: protocompile javacompile

clean:
	$(RM) *.class
	$(RM) protobuf_build/com -rf
