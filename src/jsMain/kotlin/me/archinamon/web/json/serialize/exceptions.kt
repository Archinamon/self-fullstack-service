package me.archinamon.web.json.serialize

class NotStandardTypeException(type: String) : Exception("Not a standard type: $type!")

class NotEnumTypeException : Exception("Not the Enum type!")
