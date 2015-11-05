package com.realcomp.data.conversion;

import com.realcomp.data.Operation;
import com.realcomp.data.record.Record;

public interface RecordConverter extends Operation {

    Record convert(Record value) throws ConversionException;
}

