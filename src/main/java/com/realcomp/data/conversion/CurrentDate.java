package com.realcomp.data.conversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("currentDate")
public class CurrentDate extends ComplexConverter{

    private Calendar now;
    private String format;
    private DateFormat formatter;
    
    public CurrentDate(){
        super();
        now = Calendar.getInstance();
        format = "yyyyMMdd";
        formatter = getFormatter(format);
    }
    
    public CurrentDate(String format){
        super();
        now = Calendar.getInstance();
        this.format = format;
        formatter = getFormatter(format);
    }
    
    @Override
    public CurrentDate copyOf(){
        return new CurrentDate(format);
    }

    /**
     *
     * @param value ignored
     * @return
     * @throws ConversionException
     */
    @Override
    public Object convert(Object value) throws ConversionException{
        return formatter.format(now.getTime());
    }

    
    protected DateFormat getFormatter(String format) {

        DateFormat dateFormat = null;

        if (format.equalsIgnoreCase("short"))
            dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        else if (format.equalsIgnoreCase("medium"))
            dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        else if (format.equalsIgnoreCase("long"))
            dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        else if (format.equalsIgnoreCase("full"))
            dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        else
            dateFormat = new SimpleDateFormat(format);

        return dateFormat;
    }


    public String getFormat() {
        return format;
    }

    /**
     * format one of:
     * <ul>
     *  <li>short</li>
     *  <li>medium</li>
     *  <li>long</li>
     *  <li>full</li>
     *  <li>format recognized by SimpleDateFormat</li>
     * </ul>
     * @param format (default: yyyyMMdd)
     */
    public void setFormat(String format) {
        this.format = format;
        this.formatter = getFormatter(format);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CurrentDate other = (CurrentDate) obj;
        if ((this.format == null) ? (other.format != null) : !this.format.equals(other.format))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.format != null ? this.format.hashCode() : 0);
        return hash;
    }
}
