package client;

import physics.Vect;
import physics.Angle;

/**
 * for doing more exotic physics simulation
 */
public class VectField {
    
    private Vect[][] field;
    //internal representation
    //rep invariant -- field is square, has non-zero dimensions,
    //                 and all internal Vects are not null
    
    /**
     * construct a new uniform field with the specified magnitudes
     * the field will have a default dimension of 25x25
     * 
     * @param x the x component
     * @param y the y component
     */
    public VectField(double x, double y){
        this(25,new Vect(x,y),0,0);
    }
    
    /**
     * construct a new (potentially) wacky field with the specified
     * divergence and curl. Both divergent and curly features will
     * be created symmetric about the center of the Vect field
     * 
     * @param dimension the square dimension of the field
     *          must be > 0
     * @param uniform the uniform vector component of the field
     * @param div the divergence of the field
     * @param curl the curl of the field
     */
    public VectField(int dimension, Vect uniform, double div, double curl){
        VectField uniformField = uniformVectField(dimension, uniform);
        VectField divField = uniformDivField(dimension, div);
        VectField combinedField = add(uniformField,divField);
        VectField circField = uniformCurlField(dimension, curl);
        this.field = add(combinedField,circField).field;
        if(!checkRep()){
            throw new IllegalArgumentException(
                    "those parameters do not define an acceptable VectField");
        }
    }
    
    //private constructor directly from array
    private VectField(Vect[][] field){
        this.field = field;
    }
    
    //checkRep
    private boolean checkRep(){
        if(field.length == 0 || field[0].length == 0){
            return false;
        }
        if(field.length != field[0].length){
            return false;
        }
        for(Vect[] row : field){
            for(Vect vector : row){
                if(vector == null){
                    return false;
                }
            }
        }
        return true;
    }
    
    //checks input param and throws an appropriate exception if the dimension
    //is invalid
    private static void checkDimension(int dimension){
        if(dimension <= 0){
            throw new IllegalArgumentException(
                    "requires positive dimension: received \"" + dimension + "\"");
        }
    }
    
    /**
     * construct a new uniform vector field
     * 
     * @param dimension the square dimension of the field
     *          must be > 0
     * @param vector the vector defining the uniform field
     * @return a uniform field of vectors [vector]
     */
    public static VectField uniformVectField(int dimension, Vect vector){
        checkDimension(dimension);
        Vect[][] vectField = new Vect[dimension][dimension]; 
        for(int jj = 0; jj < vectField.length; jj++){
            for(int ii = 0; ii < vectField[0].length; ii ++){
                vectField[jj][ii] = vector;
            }
        }
        VectField toReturn = new VectField(vectField);
        assert toReturn.checkRep();
        return toReturn;
    }
    
    /**
     * creates a locally divergent field centered around position x,y in a
     * dimension X dimension VectField.  The distortion has the specified
     * uniform intensity
     * 
     * @param dimension the square dimension of the vector field
     *          must be > 0
     * @param distortionRadius the size of the distortion
     *          must be > 0 (for results)
     * @param uniform the uniform vector specifying the rest of the field
     * @param intensity the intensity of the attractive distortion
     * @param x the x position
     *          must be > 0 and < dimension
     * @param y the y position
     *          must be > 0 and < dimension
     * @return a distorted VectField
     */
    public static VectField createLocalDistortion(
            int dimension, double distortionRadius, Vect uniform, double intensity, double x, double y){
        
        Vect[][] field = new Vect[dimension][dimension];
        
        //first generate a uniform field
        for(int jj = 0; jj < field[0].length; jj++){
            for(int ii = 0; ii < field.length; ii ++){
                field[jj][ii] = uniform;
            }
        }
        
        //then create a distortion centered around the specified point
        Vect center = new Vect((int)x,(int)y);
        for(int jj = 0; jj < field.length; jj++){
            for(int ii = 0; ii < field[0].length; ii ++){
                Vect ray = center.minus(new Vect(ii,jj));
                if(ray.length() == 0){
                    field[jj][ii] = new Vect(0,0);//to avoid singular triangles
                }else if(ray.length() <= distortionRadius){
                    //change the field for all positions within the desired radius
                    Angle rayAngle = new Angle(ray.x(),ray.y());
                    field[jj][ii] = new Vect(rayAngle.cos(), rayAngle.sin()).times(-intensity);
                }
            }
        }
        return new VectField(field);
    }
    
    /**
     * construct a new divergent vectField in which all vectors have
     * length [magnitude]
     * 
     * @param dimension the square dimension of the field
     *          must be > 0
     * @param magnitude the length of the vectors
     * @return a uniform-magnitude divergent vector field
     */
    public static VectField uniformDivField(int dimension, double magnitude){
        checkDimension(dimension);
        Vect[][] divField = new Vect[dimension][dimension];
        Vect center = new Vect((dimension-1)/2., (dimension-1)/2.);
        for(int jj = 0; jj < divField.length; jj++){
            for(int ii = 0; ii < divField[0].length; ii ++){
                Vect ray = center.minus(new Vect(ii,jj));
                if(ray.length() == 0){
                    divField[jj][ii] = new Vect(0,0);//to avoid singular triangles
                }else{
                    Angle rayAngle = new Angle(ray.x(),ray.y());
                    divField[jj][ii] = new Vect(rayAngle.cos(), rayAngle.sin()).times(-magnitude);
                }
            }
        }
        VectField toReturn = new VectField(divField);
        assert toReturn.checkRep();
        return toReturn;
    }
    
    /**
     * construct an inverse square divergent vector field
     * 
     * @param dimension the square dimension of the field
     *          must be > 0
     * @param magnitude the length of the vectors which are one unit of length
     *          away from the center of the field
     * @return a divergent field with inverse square falloff in magnitude
     */
    public static VectField inverseSquareDivField(int dimension, double magnitude){
        checkDimension(dimension);
        Vect[][] divField = uniformDivField(dimension,magnitude).field;
        Vect center = new Vect((dimension-1)/2., (dimension-1)/2.);
        for(int jj = 0; jj < divField.length; jj++){
            for(int ii = 0; ii < divField[0].length; ii ++){
                double rMag = new Vect(ii,jj).minus(center).length();
                Vect scaledVect = divField[jj][ii].times(1/(rMag*rMag));
                divField[jj][ii] = scaledVect;
            }
        }
        VectField toReturn = new VectField(divField);
        assert toReturn.checkRep();
        return toReturn;
    }
    
    /**
     * construct a cyclic field with the specified uniform magnitude
     * 
     * @param dimension the square dimension of the field
     *          must be > 0
     * @param magnitude the length of the vectors in the field
     * @return a rotational vector field
     */
    public static VectField uniformCurlField(int dimension, double magnitude){
        checkDimension(dimension);
        Vect[][] circField = new Vect[dimension][dimension];
        Vect center = new Vect((dimension-1)/2., (dimension-1)/2.);
        for(int jj = 0; jj < circField.length; jj++){
            for(int ii = 0; ii < circField[0].length; ii ++){
                Vect ray = new Vect(ii,jj).minus(center);
                if(ray.length() == 0){
                    circField[jj][ii] = new Vect(0,0);//to avoid singular triangles
                }else{
                    Angle rayAngle = new Angle(ray.x(),ray.y());
                    circField[jj][ii] = new Vect(rayAngle.sin(),-rayAngle.cos()).times(magnitude);
                }
            }
        }
        VectField toReturn = new VectField(circField);
        assert toReturn.checkRep();
        return toReturn;
    }
    
    /**
     * construct a cyclic field with the specified inverse square magnitude
     * 
     * @param dimension the square dimension of the field
     *          must be > 0
     * @param magnitude the length of the vectors in the field at distance
     *          one from the center
     * @return a rotational vector field
     */
    public static VectField inverseSquareCurlField(int dimension, double magnitude){
        checkDimension(dimension);
        Vect[][] circField = uniformCurlField(dimension,magnitude).field;
        Vect center = new Vect((dimension-1)/2., (dimension-1)/2.);
        for(int jj = 0; jj < circField.length; jj++){
            for(int ii = 0; ii < circField[0].length; ii ++){
                double rMag = new Vect(ii,jj).minus(center).length();
                rMag = rMag == 0 ? 1/magnitude : rMag;
                Vect scaledVect = circField[jj][ii].times(1/(rMag*rMag));
                circField[jj][ii] = scaledVect;
            }
        }
        VectField toReturn = new VectField(circField);
        assert toReturn.checkRep();
        return toReturn;
    }
    
    /**
     * construct a vector field which is the sum of two other vector fields
     * 
     * @param fieldOne the first field to be added
     * @param fieldTwo the second field to be added
     *          must have the same dimension as fieldOne
     * @return the field which is the sum of the two fields
     */
    public static VectField add(VectField fieldOne, VectField fieldTwo){
        if(fieldOne.dimension() != fieldTwo.dimension()){
            throw new IllegalArgumentException("Vect Fields must have the same dimensions");
        }
        Vect[][] vectField = new Vect[fieldOne.dimension()][fieldOne.dimension()];
        for(int jj = 0; jj < vectField.length; jj ++){
            for(int ii = 0; ii < vectField[0].length; ii ++){
                vectField[jj][ii] = 
                        fieldOne.valueAt(ii,jj).plus(fieldTwo.valueAt(ii,jj));
            }
        }
        VectField toReturn = new VectField(vectField);
        assert toReturn.checkRep();
        return toReturn;
    }
    
    /**
     * construct a vector field which is this sum of this field and
     * another
     * 
     * @param toAdd the field to be added with this one
     * @return the sum of the two fields
     */
    public VectField plus(VectField toAdd){
        return add(this,toAdd);
    }
    
    public Vect valueAt(Vect pos){
        return valueAt(pos.x(),pos.y());
    }
    
    public Vect valueAt(double x, double y){
        return field[(int)y][(int)x];
    }
    
    public int dimension(){
        return field.length;
    }
    
    @Override public String toString(){
        String fieldString = "";
        for(Vect[] row : field){
            for(Vect vector : row){
                if(vector.x() == 0 && vector.y() == 0){
                    fieldString += " ";
                }else{
                    Angle dir = new Angle(vector.x(), vector.y());
                    if(Math.abs(dir.cos()) <= 0.26){
                        fieldString += "|";
                    }else if(Math.abs(dir.sin()) <= 0.26){
                        fieldString += "-";
                    }else if(dir.sin()*dir.cos() > 0){
                        fieldString += "\\";
                    }else{
                        fieldString += "/";
                    }
                }
            }
            fieldString += "\n";
        }
        return fieldString;
    }
    
}
