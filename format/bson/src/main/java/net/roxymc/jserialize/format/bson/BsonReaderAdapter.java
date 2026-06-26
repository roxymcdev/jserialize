package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.Reader;
import org.bson.BsonReader;

interface BsonReaderAdapter extends Reader {

    BsonReader getBsonReader();
}
