JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Camera.java \
        Face.java \
        Model.java \
        PA3.java \
        Picture.java \
        Pixel.java \
        Ray.java \
        RayTracer.java \
        Vertex.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
