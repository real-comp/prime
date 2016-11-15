package com.realcomp.prime.conversion;

import com.realcomp.prime.Operation;
import com.realcomp.prime.record.Record;

public interface RecordConverter extends Operation {

    Record convert(Record value) throws ConversionException;
}

