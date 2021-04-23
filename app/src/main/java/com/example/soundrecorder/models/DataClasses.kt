package com.example.soundrecorder.models

data class OptionId1DataClass(val optionId: String, val optionHeading: String, val hashSetOfOption: HashSet<OptionDataClass>, val hashSetOfMeter: HashSet<MeterDataClass>, val hashSetOfYard: HashSet<YardDataClass>)

data class OptionId2DataClass(val optionId: String, val optionHeading: String, val hashSetOfOption: HashSet<OptionDataClass>)

data class OptionId3DataClass(val optionId: String, val optionHeading: String, val hashSetOfOption: HashSet<OptionDataClass>)

data class OptionId4DataClass(val optionId: String, val optionHeading: String, val hashSetOfOption: HashSet<OptionDataClass>)

data class OptionId5DataClass(val optionId: String, val optionHeading: String, val hashSetOfOption: HashSet<OptionDataClass>)

data class OptionId6DataClass(val optionId: String, val optionHeading: String, val hashSetOfOption: HashSet<OptionDataClass>)

data class OptionId7DataClass(val optionId: String, val optionHeading: String, val arrayOfOption: HashSet<OptionDataClass>)

data class OptionId8DataClass(val optionId: String, val optionHeading: String, val arrayOfOption: HashSet<OptionDataClass>)

data class OptionId9DataClass(val optionId: String, val optionHeading: String, val arrayOfOption: HashSet<OptionDataClass>)

data class OptionId10DataClass(val optionId: String, val optionHeading: String, val arrayOfOption: HashSet<OptionDataClass>)

data class OptionDataClass(val optionValueId: String, val title: String, val value: String, val factor1: String, val factor2: String)

data class YardDataClass(val title: String, val value: String)

data class MeterDataClass(val title: String, val value: String)

data class Country(val countryName: String, val shortName: String)