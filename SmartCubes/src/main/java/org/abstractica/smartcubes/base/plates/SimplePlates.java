package org.abstractica.smartcubes.base.plates;

import org.abstractica.javacsg.Geometry2D;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimplePlates
{
    private final JavaCSG csg;
    private final Features features;

    public SimplePlates(JavaCSG csg, Features features)
    {
        this.csg = csg;
        this.features = features;
    }

    Geometry3D generatePlate(Set<Vector2i> filledPositions, Set<Vector2i> holes)
    {
        Geometry2D square = csg.rectangle2D(24, 24);
        List<Geometry2D> squares = new ArrayList<>();
        Set<Vector2i> ballPositions = new HashSet<>();
        for(Vector2i pos : filledPositions)
        {
            Geometry2D sq = csg.translate2D(pos.x*16 + 12, pos.y*16 + 12).transform(square);
            squares.add(sq);
            for(int bx = 0; bx <= 1; bx++)
            {
                for(int by = 0; by <= 1; by++)
                {
                    ballPositions.add(new Vector2i(pos.x + bx, pos.y + by));
                }
            }
        }
        Geometry2D plate2D = csg.union2D(squares);
        Geometry3D plate3D = csg.linearExtrude(4, false, plate2D);
        Geometry3D ball3D = features.ballAddon();
        List<Geometry3D> balls = new ArrayList<>();
        for(Vector2i pos : ballPositions)
        {
            balls.add(csg.translate3D(pos.x*16 + 4, pos.y*16 + 4, 4).transform(ball3D));
        }
        plate3D = csg.union3D(plate3D, balls);
        Geometry3D holeCutout = features.doubleClickerBaseCutout();
        List<Geometry3D> holeCutouts = new ArrayList<>();
        for(Vector2i pos : holes)
        {
            holeCutouts.add(csg.translate3D(pos.x*16 + 12, pos.y*16 + 12, 0).transform(holeCutout));
        }
        plate3D = csg.difference3D(plate3D, holeCutouts);
        return plate3D;
    }

    public static void main(String[] args) throws IOException
    {
        JavaCSG csg = JavaCSGFactory.createDefault();
        Features features = new Features(csg, 1, 128);
        SimplePlates sp = new SimplePlates(csg, features);
        Set<Vector2i> filledPositions = new HashSet<>();
        for(int x = 0; x < 5; x++)
        {
            filledPositions.add(new Vector2i(x, 0));
        }
        for(int y = 1; y < 3; y++)
        {
            filledPositions.add(new Vector2i(2, y));
        }
        Set<Vector2i> holes = new HashSet<>();
        holes.add(new Vector2i(0, 0));
        holes.add(new Vector2i(2, 0));
        holes.add(new Vector2i(4, 0));
        holes.add(new Vector2i(2, 2));
        Geometry3D geometry = sp.generatePlate(filledPositions, holes);
        csg.save3MF("OpenSCAD/testPlate.3mf", geometry);
    }
}
