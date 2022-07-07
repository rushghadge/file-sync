public class TwoDtoOneD {

    public static void main(String[] args){
        System.out.println("inside main");
        byte[] a={2,3,4,5,6,7,8,9,10};
        byte[][] b=  monoToBidi( a,3,3);
        byte[] c= bidiToMono(b);
       for(int i=0;i<c.length;i++){
        System.out.println(c[i]);
}
    }
    public static byte[][] monoToBidi( final byte[] array, final int rows, final int cols ) {
        System.out.println("inside monoToBidi");
        if (array.length != (rows*cols))
            throw new IllegalArgumentException("Invalid array length");

        byte[][] bidi = new byte[rows][cols];
        for ( int i = 0; i < rows; i++ )
            System.arraycopy(array, (i*cols), bidi[i], 0, cols);

        return bidi;
    }

    public static byte[] bidiToMono( final byte[][] array ) {
        System.out.println("inside bidiToMono");
        long rows = array.length, cols = array[0].length;
        System.out.println("rows " + rows + " Cols "+ cols);
        System.out.println("rows * cols " + rows*cols);
        byte[] mono = new byte[2000000000];
       // byte[] mono = new byte[(int)(rows*cols)];
        for ( int i = 0; i < rows; i++ )
            System.arraycopy(array[i], 0, mono, (int)(i*cols), (int)cols);
        return mono;
    }
}
