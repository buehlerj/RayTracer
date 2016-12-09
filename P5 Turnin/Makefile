JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Camera.java \
        Face.java \
        Light.java \
        Material.java \
        Model.java \
        Picture.java \
        Pixel.java \
        Ray.java \
        RayTracer.java \
        Scene.java \
        Sphere.java \
        Utils.java \
        Vertex.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
