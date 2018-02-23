package meshgen;

import math.Vector2;
import math.Vector3;
import meshgen.OBJMesh;
import meshgen.OBJFace;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.Math;
import java.util.Scanner;

class MeshGen {
    static String stringG = ""; // -g
    static int n = 32; // (optional) -n
    static int m = 16; // (optional) -m
    static float r = (float)0.25; // (optional) -r
    static String stringI = ""; // -i
    static String stringO = ""; // -o

    public static void main(String[] args) {
        int q = 0;
        while (q < args.length) {
            //parse option
            String nextArgument = q + 1 < args.length ? args[q + 1] : null;
            switch ( args[q] ) {
                case "-g": stringG = nextArgument;q += 2; break;
                case "-n":
                    try {
                        n = Integer.parseInt(nextArgument);
                        q += 2;
                    } catch ( NumberFormatException e ) {
                        System.err.println("Error: when parsing -n parameter, expected an integer, got " + nextArgument);
                        System.exit(-1);
                    }
                    break;
                case "-m":
                    try {
                        m = Integer.parseInt(nextArgument);
                        q += 2;
                    } catch ( NumberFormatException e ) {
                        System.err.println("Error: when parsing -m parameter, expected an integer, got " + nextArgument);
                        System.exit(-1);
                    }
                    break;
                case "-r":
                    try {
                        r = Float.parseFloat(nextArgument);
                        q += 2;
                    } catch ( NumberFormatException e ) {
                        System.err.println("Error: when parsing -r parameter, expected a float, got " + nextArgument);
                        System.exit(-1);
                    }
                    break;
                case "-i": stringI = nextArgument;q += 2; break;
                case "-o": stringO = nextArgument;q += 2; break;
                default:
                    //in case current argument is not recognized
                    q++;
            }
        }
        if (stringG.equals("cylinder")){
          cylinder();
        }
        else if (stringG.equals("sphere")){
          sphere();
        }
        else if (stringG.equals("torus"))
        {
          torus();
        }
        else if (stringG.equals("")){
          if (stringI.equals(""))
          {System.out.println("Unacceptable input!\n");}
          else
          {
            try{
              boolean result;
              OBJMesh add_normal = compute_graphic("../data/" + stringI);
              OBJMesh reference;
              if (stringI.equals("horse-nonorms.obj")){
                reference = new OBJMesh("../data/horse-norms-reference.obj");
                result = OBJMesh.compare(add_normal, reference, true, (float)0.00001);
              }
              else if(stringI.equals("bunny-nonorms.obj")){
                reference = new OBJMesh("../data/bunny-norms-reference.obj");
                result = OBJMesh.compare(add_normal, reference, true, (float)0.00001);
              }
              add_normal.writeOBJ(stringO);
            }catch(Exception e){}
          }
        }
    }

    public static void cylinder(){
      int m = n;
      OBJMesh cylinder = new OBJMesh();
      Vector3 top = new Vector3(0, 1, 0);
      Vector2 top_uv = new Vector2((float)0.75, (float)0.75); //uv for the centre point of upper
      Vector3 top_normal = new Vector3(0, 1, 0);
      cylinder.positions.add(top);
      cylinder.uvs.add(top_uv);
      cylinder.normals.add(top_normal);

      int index = 0;
      while (index < m) {
        Vector3 next = new Vector3(1);
        Vector2 next_uv1 = new Vector2((float)0.75);    //uv for the upper
        Vector2 next_uv2 = new Vector2(0, (float)0.5);  //uv for the side
        Vector3 next_normal = new Vector3(0); // normals for the upper and buttom
        next.x = -(float)Math.sin((Math.PI*2/m)*index);
        next.z = -(float)Math.cos((Math.PI*2/m)*index);
        next_normal.x = -(float)Math.sin((Math.PI*2/m)*index);
        next_normal.z = -(float)Math.cos((Math.PI*2/m)*index);
        cylinder.positions.add(next);
        next_uv1.add(-(float)(0.25*Math.sin((Math.PI*2/m)*index)), (float)(0.25*Math.cos((Math.PI*2/m)*index)));
        next_uv2.add(((float)1/m)*index,0);
        cylinder.uvs.add(next_uv1);
        cylinder.uvs.add(next_uv2);
        cylinder.normals.add(next_normal);
        index++;
      }
      Vector3 buttom = new Vector3(0, -1, 0);
      Vector2 buttoom_vu = new Vector2((float)0.25,(float)0.75);
      Vector3 buttom_normal = new Vector3(0, -1, 0);
      cylinder.positions.add(buttom);
      cylinder.uvs.add(buttoom_vu);
      cylinder.normals.add(buttom_normal);

      index = 0;
      while (index < m) {
        Vector3 next = new Vector3(-1);
        Vector2 next_uv1 = new Vector2((float)0.25, (float)0.75);  //uv for the buttom
        Vector2 next_uv2 = new Vector2(0,0); //uv for the side
        next.x = -(float)Math.sin((Math.PI*2/m)*index);
        next.z = -(float)Math.cos((Math.PI*2/m)*index);
        cylinder.positions.add(next);
        next_uv1.add(-(float)(0.25*Math.sin((Math.PI*2/m)*index)), -(float)(0.25*Math.cos((Math.PI*2/m)*index)));
        next_uv2.add(((float)1/m)*index,0);
        cylinder.uvs.add(next_uv1);
        cylinder.uvs.add(next_uv2);
        index++;
      }
      cylinder.uvs.add(new Vector2(1,(float)0.5));
      cylinder.uvs.add(new Vector2(1,0));

      index = 0;
      while (index < m) {
        OBJFace face_upper = new OBJFace(3, true, true);
        face_upper.setVertex(0, 0, 0, 0);
        face_upper.setVertex(1, index+1, 2*index+1, 0);
        face_upper.setVertex(2, index+2, 2*(index+1)+1, 0);
        if (index == m-1) face_upper.setVertex(2, 1, 1, 0);
        cylinder.faces.add(face_upper);
        OBJFace side_1 = new OBJFace(3, true, true);
        side_1.setVertex(0, index+1, 2*index+2, index+1);
        side_1.setVertex(1, index+2+m, 2*index+2*m+3, index+1);
        //side_1.setVertex(2, index+3+m, 2*(index+1)+2*m+3, index+2);
        side_1.setVertex(2, index+2, 2*(index+1)+2, index+2);
        //if (index == m-1) side_1.setVertex(2, 2, cylinder.uvs.size()-1, 1);
        if (index == m-1) side_1.setVertex(2, 1, cylinder.uvs.size()-2, 1);
        cylinder.faces.add(side_1);
        OBJFace side_2 = new OBJFace(3, true, true);
        side_2.setVertex(0, index+3+m, 2*(index+1)+2*m+3, index+2);
        side_2.setVertex(1, index+2, 2*(index+1)+2, index+2);
        //side_2.setVertex(2, index+1, 2*index+2, index+1);
        side_2.setVertex(2, index+2+m, 2*index+2*m+3, index+1);
        // if (index == m-1) {
        //   side_2.setVertex(1, 1, cylinder.uvs.size()-2, 1);
        //   side_2.setVertex(0,m+2, cylinder.uvs.size()-1, 2);
        // }
        if (index == m-1) {
          side_2.setVertex(1, 1, cylinder.uvs.size()-2, 1);
          side_2.setVertex(0, 2+m, cylinder.uvs.size()-1, 1);
        }
        cylinder.faces.add(side_2);
        OBJFace face_buttom = new OBJFace(3, true, true);
        face_buttom.setVertex(0, m+1, 2*m+1, m+1);
        face_buttom.setVertex(2, index+2+m, 2*(index+m)+2, m+1);
        face_buttom.setVertex(1, index+3+m, 2*(index+1+m)+2, m+1);
        if (index == m-1) face_buttom.setVertex(1, m+2, 2+2*m, m+1);
        cylinder.faces.add(face_buttom);
        index++;
      }
      cylinder.isValid(true);
      try {
        cylinder.writeOBJ(stringO);
      }
      catch (Exception e) {}
      OBJMesh cylinder_reference = new OBJMesh();
      try
      {
        cylinder_reference.parseOBJ("../data/cylinder-reference.obj");
      }catch(Exception e){}
      boolean result;
      result = cylinder.compare(cylinder_reference, cylinder, true, (float)1.0E-5);
    }

    public static void sphere(){
      boolean b1 = true;//hasUVs
      boolean b2 = true;//hasNormals
      OBJMesh sphere = new OBJMesh();

      //add positions and normals
      Vector3 point = new Vector3(0, 1, 0);
      sphere.positions.add(point);
      sphere.normals.add(point);
      double delta_lati = Math.PI / m;
      double delta_long = 2 * Math.PI / n;
      for (int i = 0; i < (m - 1); i++)
      {
        for (int j = 0; j < n; j++)
        {
          Vector3 newpoint = new Vector3(1);
          double theta = Math.PI / 2 - (i + 1) * delta_lati;
          double phi = j * delta_long + Math.PI;
          newpoint.x = (float)Math.cos(theta) * (float)Math.sin(phi);
          newpoint.y = (float)Math.sin(theta);
          newpoint.z = (float)Math.cos(theta) * (float)Math.cos(phi);
          sphere.positions.add(newpoint);
          sphere.normals.add(newpoint);
        }
      }
      Vector3 point1 = new Vector3(0, -1, 0);
      sphere.positions.add(point1);
      sphere.normals.add(point1);

      // add uvs
      double delta_u = 1.0 / n;
      double delta_v = 1.0 / m;
      for (int j = 0; j < n; j++)
      {
        Vector2 p = new Vector2(1);
        double u = j * delta_u;
        double v = 1;
        p.x = (float) u;
        p.y = (float) v;
        sphere.uvs.add(p);
      }
      for (int i = 0; i < (m - 1); i++)
      {
        for (int j = 0; j <= n; j++)
        {
          Vector2 newp = new Vector2(1);
          double u = 0 + j * delta_u;
          double v = 1 - (i + 1) * delta_v;
          newp.x = (float) u;
          newp.y = (float) v;
          sphere.uvs.add(newp);
        }
      }
      for (int j = 0; j < n; j++)
      {
        Vector2 p1 = new Vector2(1);
        double u = 0 + (j + 1) * delta_u;
        double v = 0;
        p1.x = (float) u;
        p1.y = (float) v;
        sphere.uvs.add(p1);
      }

      //add faces
      for (int i = 0; i < m; i++)
      {
        for (int j = 0; j < n; j++)
        {
          if (i == 0)
          {
            OBJFace face = new OBJFace(3, b1, b2);
            if (j == (n - 1))
            {
              face.positions = new int[]{0, j + 1, 1};
              face.uvs = new int[]{j, j + n, j + n + 1};
              face.normals = new int[]{0, j + 1, 1};
              face.indexBase = 0;
              sphere.faces.add(face);
            }
            else
            {
              face.positions = new int[]{0, j + 1, j + 2};
              face.uvs = new int[]{j, j + n, j + n + 1};
              face.normals = new int[]{0, j + 1, j + 2};
              face.indexBase = 0;
              sphere.faces.add(face);
            }
          }
          else if (i == (m - 1))
          {
            OBJFace face = new OBJFace(3, b1, b2);
            if (j == (n - 1))
            {
              face.positions = new int[]{(m - 1) * n + 1, (m - 2) * n + 1, (m - 2) * n + j + 1};
              face.uvs = new int[]{m * n + m + j - 1, m * n + m - n + j - 1, m * n + m - n + j - 2};
              face.normals = new int[]{(m - 1) * n + 1, (m - 2) * n + 1, (m - 2) * n + j + 1};
              face.indexBase = 0;
              sphere.faces.add(face);
            }
            else
            {
              face.positions = new int[]{(m - 1) * n + 1, (m - 2) * n + j + 2, (m - 2) * n + j + 1};
              face.uvs = new int[]{m * n + m + j - 1, m * n + m - n + j - 1, m * n + m - n + j - 2};
              face.normals = new int[]{(m - 1) * n + 1, (m - 2) * n + j + 2, (m - 2) * n + j + 1};
              face.indexBase = 0;
              sphere.faces.add(face);
            }
          }
          else
          {
            int a, b, c, d, x1, y1, x2, y2;
            a = (i - 1) * n + j + 1;
            b = (i - 1) * n + j + 2;
            c = i * n + j + 1;
            d = i * n + j + 2;
            x1 = (i - 1) * n + 1;
            y1 = i * n + 1;
            x2 = i * n;
            y2 = (i + 1) * n;
            OBJFace face1 = new OBJFace(3, b1, b2);
            if (j == (n - 1))
            {
              face1.positions = new int[]{x2, y1, x1};
              face1.uvs = new int[]{i * n + i + j - 1, (i + 1) * n + i + j + 1, i * n + i + j};
              face1.normals = new int[]{x2, y1, x1};
              face1.indexBase = 0;
              sphere.faces.add(face1);
            }
            else
            {
              face1.positions = new int[]{a, d, b};
              face1.uvs = new int[]{i * n + i + j - 1, (i + 1) * n + i + j + 1, i * n + i + j};
              face1.normals = new int[]{a, d, b};
              face1.indexBase = 0;
              sphere.faces.add(face1);
            }
            OBJFace face2 = new OBJFace(3, b1, b2);
            if (j == (n - 1))
            {
              face2.positions = new int[]{x2, y2, y1};
              face2.uvs = new int[]{i * n + i + j - 1, (i + 1) * n + i + j, (i + 1) * n + i + j + 1};
              face2.normals = new int[]{x2, y2, y1};
              face2.indexBase = 0;
              sphere.faces.add(face2);
            }
            else
            {
              face2.positions = new int[]{a, c, d};
              face2.uvs = new int[]{i * n + i + j - 1, (i + 1) * n + i + j, (i + 1) * n + i + j + 1};
              face2.normals = new int[]{a, c, d};
              face2.indexBase = 0;
              sphere.faces.add(face2);
            }
          }
        }
      }
      sphere.isValid(true);
      try
      {
        sphere.writeOBJ(stringO);
      }catch(Exception ex){}
      OBJMesh sphere_reference = new OBJMesh();
      try
      {
        sphere_reference.parseOBJ("../data/sphere-reference.obj");
      }catch(Exception e){}
      boolean result;
      result = sphere.compare(sphere_reference, sphere, true, (float)1.0E-5);
    }

    public static void torus(){
      boolean b1 = true;//hasUVs
      boolean b2 = true;//hasNormals
      OBJMesh torus = new OBJMesh();

      //add positions and normals
      double delta_m = 2 * Math.PI / m;
      double delta_n = 2 * Math.PI / n;
      for (int i = 0; i < n; i++)
      {
        for (int j = 0; j < m; j++)
        {
          Vector3 newpoint = new Vector3(1);
          double phi = i * delta_n + Math.PI;
          double theta = j * delta_m + Math.PI;
          newpoint.x = (float)(((1 + r) / 2 + (1 - r) * (float)Math.cos(theta) / 2) * (float)Math.sin(phi));
          newpoint.y = (float)(1 - r) * (float)Math.sin(theta) / 2;
          newpoint.z = (float)(((1 + r) / 2 + (1 - r) * (float)Math.cos(theta) / 2) * (float)Math.cos(phi));
          torus.positions.add(newpoint);
          Vector3 originpoint = new Vector3(1);
          originpoint.x = (float)((1 + r) * (float)Math.sin(phi) / 2);
          originpoint.y = (float)0.0;
          originpoint.z = (float)((1 + r) * (float)Math.cos(phi) / 2);
          Vector3 normal = new Vector3(1);
          float x = newpoint.x - originpoint.x;
          float y = newpoint.y - originpoint.y;
          float z = newpoint.z - originpoint.z;
          normal.x = (float) x / (float)Math.sqrt(x * x + y * y + z * z);
          normal.y = (float) y / (float)Math.sqrt(x * x + y * y + z * z);
          normal.z = (float) z / (float)Math.sqrt(x * x + y * y + z * z);
          torus.normals.add(normal);
        }
      }

      // add uvs
      double delta_u = 1.0 / n;
      double delta_v = 1.0 / m;
      for (int i = 0; i <= m; i++)
      {
        for (int j = 0; j <= n; j++)
        {
          Vector2 newp = new Vector2(1);
          double u = 0 + j * delta_u;
          double v = 0 + i * delta_v;
          newp.x = (float) u;
          newp.y = (float) v;
          torus.uvs.add(newp);
        }
      }

      //add faces
      for (int i = 0; i < n; i++)
      {
        for (int j = 0; j < m; j++)
        {
          int a, b, c, d, a0, b0, c0, d0;
          a = i * m + j;
          b = i * m + j + 1;
          c = i * m + j + m;
          d = i * m + j + m + 1;
          a0 = i * m + i + j;
          b0 = i * m + i + j + 1;
          c0 = i * m + i + j + m + 1;
          d0 = i * m + i + j + m + 2;
          if (i == (n - 1) && j != (m - 1))
          {
            c = j;
            d = j + 1;
          }
          else if (j == (m - 1) && i != (n - 1))
          {
            b = i * m;
            d = i * m + m;
          }
          else if (i == (n - 1) && j == (m - 1))
          {
            b = (n - 1) * m;
            c = m - 1;
            d = 0;
          }
          OBJFace face1 = new OBJFace(3, b1, b2);
          face1.positions = new int[]{a, c, d};
          face1.uvs = new int[]{a0, c0, d0};
          face1.normals = new int[]{a, c, d};
          face1.indexBase = 0;
          torus.faces.add(face1);
          OBJFace face2 = new OBJFace(3, b1, b2);
          face2.positions = new int[]{a, d, b};
          face2.uvs = new int[]{a0, d0, b0};
          face2.normals = new int[]{a, d, b};
          face2.indexBase = 0;
          torus.faces.add(face2);
        }
      }
      torus.isValid(true);
      try
      {
        torus.writeOBJ(stringO);
      }catch(Exception ex){}
    }

    public static OBJMesh compute_graphic (String input_name) {
      try{
        OBJMesh origin = new OBJMesh(input_name);
        for(int index = 0; index < origin.positions.size(); index++) {
          origin.normals.add(new Vector3(0,0,0));
        }
        for (OBJFace face: origin.faces) {
          Vector3 normal_toAdd = Compute_normal(face, origin);
          int count = 0;
          for (int index: face.positions) {
            if(count == 0) face.normals = new int[3];
            face.normals[count] = index;
            count++;
            origin.normals.get(index).add(normal_toAdd);
          }
        }

        for (Vector3 nor: origin.normals) {
            nor.normalize();
        }
        return origin;
      }
      catch(Exception e){return null;}
    }

    private static Vector3 Compute_normal (OBJFace face, OBJMesh origin) {
      Vector3 point_0 = origin.positions.get(face.positions[0]).clone();
      Vector3 point_1 = origin.positions.get(face.positions[1]).clone();
      Vector3 point_2 = origin.positions.get(face.positions[2]).clone();
      Vector3 v_01 = point_1.sub(point_0).clone();
      Vector3 v_02 = point_2.sub(point_0).clone();
      Vector3 nor = new Vector3(((v_01.y)*(v_02.z)-(v_01.z)*(v_02.y)),((v_01.z)*(v_02.x)-(v_01.x)*(v_02.z)),
      ((v_01.x)*(v_02.y)-(v_02.x)*(v_01.y)));
      return nor.normalize();
    }
}
