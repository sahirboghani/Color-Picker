package gov.sahir.colorpicker;

/**
 * Created by Sahir on 4/19/2015.
 */
public interface ColorCallBack {

    void setHue(float hue);
    void setSaturation(float saturation);
    void setValue(float value);
    float getHue();
    float getSaturation();
    float getValue();

}
