JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	webserver.java

default: classes

compile: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
