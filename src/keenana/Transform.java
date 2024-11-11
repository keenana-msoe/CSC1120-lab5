/*
 * Course: CSC 1120
 * Term: Spring 2024
 * Assignment: Lab 5
 * Name: Andrew Keenan
 * Created: 2-14-24
 */
package keenana;

/**
 * transform interface that does the transformation math on each image pixel value
 */
public interface Transform {
    /**
     * applies a transfromation
     * @param nums an array of numbers
     * @return a single integer with the transformation
     */
    int apply(int[] nums);
}
