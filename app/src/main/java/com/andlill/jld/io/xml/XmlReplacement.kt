package com.andlill.jld.io.xml

import org.xmlpull.v1.XmlPullParser

object XmlReplacement {

    // Get JMdict readable entity replacement text.
    fun getValue(text: String): String {
        return when (text) {
            "&adj-i;" -> "Adjective"
            "&adj-ix;" -> "Adjective"
            "&adj-na;" -> "Na-adjective"
            "&adj-no;" -> "No-adjective"
            "&adj-t;" -> "Adjective"
            "&adv;" -> "Adverb"
            "&adv-to;" -> "Adverb"
            "&aux-adj;" -> "Auxiliary adjective"
            "&aux-v;" -> "Auxiliary verb"
            "&conj;" -> "Conjunction"
            "&cop;" -> "Copula"
            "&ctr;" -> "Counter"
            "&exp;" -> "Expression"
            "&n;" -> "Noun"
            "&n-adv;" -> "Adverbial noun"
            "&n-pr;" -> "Proper noun"
            "&n-pref;" -> "Prefix noun"
            "&n-suf;" -> "Suffix noun"
            "&n-t;" -> "Temporal noun"
            "&pref;" -> "Prefix"
            "&suf;" -> "Suffix"
            "&v-unspec;" -> "Unspecified verb"
            "&v1;" -> "Ichidan verb"
            "&v1-s;" -> "Ichidan verb"
            "&v2a-s;" -> "Nidan verb"
            "&v2b-k;" -> "Nidan verb"
            "&v2b-s;" -> "Nidan verb"
            "&v2d-k;" -> "Nidan verb"
            "&v2d-s;" -> "Nidan verb"
            "&v2g-k;" -> "Nidan verb"
            "&v2g-s;" -> "Nidan verb"
            "&v2h-k;" -> "Nidan verb"
            "&v2h-s;" -> "Nidan verb"
            "&v2k-k;" -> "Nidan verb"
            "&v2k-s;" -> "Nidan verb"
            "&v2m-k;" -> "Nidan verb"
            "&v2m-s;" -> "Nidan verb"
            "&v2n-s;" -> "Nidan verb"
            "&v2r-k;" -> "Nidan verb"
            "&v2r-s;" -> "Nidan verb"
            "&v2s-s;" -> "Nidan verb"
            "&v2t-k;" -> "Nidan verb"
            "&v2t-s;" -> "Nidan verb"
            "&v2w-s;" -> "Nidan verb"
            "&v2y-k;" -> "Nidan verb"
            "&v2y-s;" -> "Nidan verb"
            "&v2z-s;" -> "Nidan verb"
            "&v4b;" -> "Yodan verb"
            "&v4g;" -> "Yodan verb"
            "&v4h;" -> "Yodan verb"
            "&v4k;" -> "Yodan verb"
            "&v4m;" -> "Yodan verb"
            "&v4n;" -> "Yodan verb"
            "&v4r;" -> "Yodan verb"
            "&v4s;" -> "Yodan verb"
            "&v4t;" -> "Yodan verb"
            "&v5aru;" -> "Godan verb"
            "&v5b;" -> "Godan verb"
            "&v5g;" -> "Godan verb"
            "&v5k;" -> "Godan verb"
            "&v5k-s;" -> "Godan verb"
            "&v5m;" -> "Godan verb"
            "&v5n;" -> "Godan verb"
            "&v5r;" -> "Godan verb"
            "&v5r-i;" -> "Godan verb"
            "&v5s;" -> "Godan verb"
            "&v5t;" -> "Godan verb"
            "&v5u;" -> "Godan verb"
            "&v5u-s;" -> "Godan verb"
            "&v5uru;" -> "Godan verb"
            "&vi;" -> "Intransitive verb"
            "&vk;" -> "Kuru verb"
            "&vn;" -> "Irregular verb"
            "&vr;" -> "Irregular verb"
            "&vs;" -> "Irregular verb"
            "&vs-i;" -> "Suru verb"
            "&vs-s;" -> "Irregular verb"
            "&vt;" -> "Transitive verb"
            else -> text
        }
    }

    // JMdict Entity Replacement Definitions.
    @Suppress("SpellCheckingInspection")
    fun setEntityReplacementText(parser: XmlPullParser) {
        // <dial>
        parser.defineEntityReplacementText("hob", "&hob;")
        parser.defineEntityReplacementText("ksb", "&ksb;")
        parser.defineEntityReplacementText("ktb", "&ktb;")
        parser.defineEntityReplacementText("kyb", "&kyb;")
        parser.defineEntityReplacementText("kyu", "&kyu;")
        parser.defineEntityReplacementText("nab", "&nab;")
        parser.defineEntityReplacementText("osb", "&osb;")
        parser.defineEntityReplacementText("rkb", "&rkb;")
        parser.defineEntityReplacementText("thb", "&thb;")
        parser.defineEntityReplacementText("tsb", "&tsb;")
        parser.defineEntityReplacementText("tsug", "&tsug;")
        // <field>
        parser.defineEntityReplacementText("agric", "&agric;")
        parser.defineEntityReplacementText("anat", "&anat;")
        parser.defineEntityReplacementText("archeol", "&archeol;")
        parser.defineEntityReplacementText("archit", "&archit;")
        parser.defineEntityReplacementText("art", "&art;")
        parser.defineEntityReplacementText("astron", "&astron;")
        parser.defineEntityReplacementText("audvid", "&audvid;")
        parser.defineEntityReplacementText("aviat", "&aviat;")
        parser.defineEntityReplacementText("baseb", "&baseb;")
        parser.defineEntityReplacementText("biochem", "&biochem;")
        parser.defineEntityReplacementText("biol", "&biol;")
        parser.defineEntityReplacementText("bot", "&bot;")
        parser.defineEntityReplacementText("Buddh", "&Buddh;")
        parser.defineEntityReplacementText("bus", "&bus;")
        parser.defineEntityReplacementText("chem", "&chem;")
        parser.defineEntityReplacementText("Christn", "&Christn;")
        parser.defineEntityReplacementText("comp", "&comp;")
        parser.defineEntityReplacementText("cryst", "&cryst;")
        parser.defineEntityReplacementText("ecol", "&ecol;")
        parser.defineEntityReplacementText("econ", "&econ;")
        parser.defineEntityReplacementText("elec", "&elec;")
        parser.defineEntityReplacementText("electr", "&electr;")
        parser.defineEntityReplacementText("embryo", "&embryo;")
        parser.defineEntityReplacementText("engr", "&engr;")
        parser.defineEntityReplacementText("ent", "&ent;")
        parser.defineEntityReplacementText("finc", "&finc;")
        parser.defineEntityReplacementText("fish", "&fish;")
        parser.defineEntityReplacementText("food", "&food;")
        parser.defineEntityReplacementText("gardn", "&gardn;")
        parser.defineEntityReplacementText("genet", "&genet;")
        parser.defineEntityReplacementText("geogr", "&geogr;")
        parser.defineEntityReplacementText("geol", "&geol;")
        parser.defineEntityReplacementText("geom", "&geom;")
        parser.defineEntityReplacementText("go", "&go;")
        parser.defineEntityReplacementText("golf", "&golf;")
        parser.defineEntityReplacementText("gramm", "&gramm;")
        parser.defineEntityReplacementText("grmyth", "&grmyth;")
        parser.defineEntityReplacementText("hanaf", "&hanaf;")
        parser.defineEntityReplacementText("horse", "&horse;")
        parser.defineEntityReplacementText("law", "&law;")
        parser.defineEntityReplacementText("ling", "&ling;")
        parser.defineEntityReplacementText("logic", "&logic;")
        parser.defineEntityReplacementText("MA", "&MA;")
        parser.defineEntityReplacementText("mahj", "&mahj;")
        parser.defineEntityReplacementText("math", "&math;")
        parser.defineEntityReplacementText("mech", "&mech;")
        parser.defineEntityReplacementText("med", "&med;")
        parser.defineEntityReplacementText("met", "&met;")
        parser.defineEntityReplacementText("mil", "&mil;")
        parser.defineEntityReplacementText("music", "&music;")
        parser.defineEntityReplacementText("ornith", "&ornith;")
        parser.defineEntityReplacementText("paleo", "&paleo;")
        parser.defineEntityReplacementText("pathol", "&pathol;")
        parser.defineEntityReplacementText("pharm", "&pharm;")
        parser.defineEntityReplacementText("phil", "&phil;")
        parser.defineEntityReplacementText("photo", "&photo;")
        parser.defineEntityReplacementText("physics", "&physics;")
        parser.defineEntityReplacementText("physiol", "&physiol;")
        parser.defineEntityReplacementText("print", "&print;")
        parser.defineEntityReplacementText("psych", "&psych;")
        parser.defineEntityReplacementText("Shinto", "&Shinto;")
        parser.defineEntityReplacementText("shogi", "&shogi;")
        parser.defineEntityReplacementText("sports", "&sports;")
        parser.defineEntityReplacementText("stat", "&stat;")
        parser.defineEntityReplacementText("sumo", "&sumo;")
        parser.defineEntityReplacementText("telec", "&telec;")
        parser.defineEntityReplacementText("tradem", "&tradem;")
        parser.defineEntityReplacementText("vidg", "&vidg;")
        parser.defineEntityReplacementText("zool", "&zool;")
        // <re_inf>
        parser.defineEntityReplacementText("gikun", "&gikun;")
        parser.defineEntityReplacementText("ik", "&ik;")
        parser.defineEntityReplacementText("ok", "&ok;")
        parser.defineEntityReplacementText("uK", "&uK;")
        // <ke_inf>
        parser.defineEntityReplacementText("ateji", "&ateji;")
        parser.defineEntityReplacementText("ik", "&ik;")
        parser.defineEntityReplacementText("iK", "&iK;")
        parser.defineEntityReplacementText("io", "&io;")
        parser.defineEntityReplacementText("oK", "&oK;")
        // <misc>
        parser.defineEntityReplacementText("abbr", "&abbr;")
        parser.defineEntityReplacementText("arch", "&arch;")
        parser.defineEntityReplacementText("char", "&char;")
        parser.defineEntityReplacementText("chn", "&chn;")
        parser.defineEntityReplacementText("col", "&col;")
        parser.defineEntityReplacementText("company", "&company;")
        parser.defineEntityReplacementText("creat", "&creat;")
        parser.defineEntityReplacementText("dated", "&dated;")
        parser.defineEntityReplacementText("dei", "&dei;")
        parser.defineEntityReplacementText("derog", "&derog;")
        parser.defineEntityReplacementText("ev", "&ev;")
        parser.defineEntityReplacementText("fam", "&fam;")
        parser.defineEntityReplacementText("fem", "&fem;")
        parser.defineEntityReplacementText("fict", "&fict;")
        parser.defineEntityReplacementText("given", "&given;")
        parser.defineEntityReplacementText("hist", "&hist;")
        parser.defineEntityReplacementText("hon", "&hon;")
        parser.defineEntityReplacementText("hum", "&hum;")
        parser.defineEntityReplacementText("id", "&id;")
        parser.defineEntityReplacementText("joc", "&joc;")
        parser.defineEntityReplacementText("leg", "&leg;")
        parser.defineEntityReplacementText("litf", "&litf;")
        parser.defineEntityReplacementText("m-sl", "&m-sl;")
        parser.defineEntityReplacementText("male", "&male;")
        parser.defineEntityReplacementText("myth", "&myth;")
        parser.defineEntityReplacementText("net-sl", "&net-sl;")
        parser.defineEntityReplacementText("obj", "&obj;")
        parser.defineEntityReplacementText("obs", "&obs;")
        parser.defineEntityReplacementText("obsc", "&obsc;")
        parser.defineEntityReplacementText("on-mim", "&on-mim;")
        parser.defineEntityReplacementText("organization", "&organization;")
        parser.defineEntityReplacementText("oth", "&oth;")
        parser.defineEntityReplacementText("person", "&person;")
        parser.defineEntityReplacementText("place", "&place;")
        parser.defineEntityReplacementText("poet", "&poet;")
        parser.defineEntityReplacementText("pol", "&pol;")
        parser.defineEntityReplacementText("product", "&product;")
        parser.defineEntityReplacementText("proverb", "&proverb;")
        parser.defineEntityReplacementText("quote", "&quote;")
        parser.defineEntityReplacementText("rare", "&rare;")
        parser.defineEntityReplacementText("relig", "&relig;")
        parser.defineEntityReplacementText("sens", "&sens;")
        parser.defineEntityReplacementText("serv", "&serv;")
        parser.defineEntityReplacementText("sl", "&sl;")
        parser.defineEntityReplacementText("station", "&station;")
        parser.defineEntityReplacementText("surname", "&surname;")
        parser.defineEntityReplacementText("uk", "&uk;")
        parser.defineEntityReplacementText("unclass", "&unclass;")
        parser.defineEntityReplacementText("vulg", "&vulg;")
        parser.defineEntityReplacementText("work", "&work;")
        parser.defineEntityReplacementText("X", "&X;")
        parser.defineEntityReplacementText("yoji", "&yoji;")
        // <pos>
        parser.defineEntityReplacementText("adj-f", "&adj-f;")
        parser.defineEntityReplacementText("adj-i", "&adj-i;")
        parser.defineEntityReplacementText("adj-ix", "&adj-ix;")
        parser.defineEntityReplacementText("adj-kari", "&adj-kari;")
        parser.defineEntityReplacementText("adj-ku", "&adj-ku;")
        parser.defineEntityReplacementText("adj-na", "&adj-na;")
        parser.defineEntityReplacementText("adj-nari", "&adj-nari;")
        parser.defineEntityReplacementText("adj-no", "&adj-no;")
        parser.defineEntityReplacementText("adj-pn", "&adj-pn;")
        parser.defineEntityReplacementText("adj-shiku", "&adj-shiku;")
        parser.defineEntityReplacementText("adj-t", "&adj-t;")
        parser.defineEntityReplacementText("adv", "&adv;")
        parser.defineEntityReplacementText("adv-to", "&adv-to;")
        parser.defineEntityReplacementText("aux", "&aux;")
        parser.defineEntityReplacementText("aux-adj", "&aux-adj;")
        parser.defineEntityReplacementText("aux-v", "&aux-v;")
        parser.defineEntityReplacementText("conj", "&conj;")
        parser.defineEntityReplacementText("cop", "&cop;")
        parser.defineEntityReplacementText("ctr", "&ctr;")
        parser.defineEntityReplacementText("exp", "&exp;")
        parser.defineEntityReplacementText("int", "&int;")
        parser.defineEntityReplacementText("n", "&n;")
        parser.defineEntityReplacementText("n-adv", "&n-adv;")
        parser.defineEntityReplacementText("n-pr", "&n-pr;")
        parser.defineEntityReplacementText("n-pref", "&n-pref;")
        parser.defineEntityReplacementText("n-suf", "&n-suf;")
        parser.defineEntityReplacementText("n-t", "&n-t;")
        parser.defineEntityReplacementText("num", "&num;")
        parser.defineEntityReplacementText("pn", "&pn;")
        parser.defineEntityReplacementText("pref", "&pref;")
        parser.defineEntityReplacementText("prt", "&prt;")
        parser.defineEntityReplacementText("suf", "&suf;")
        parser.defineEntityReplacementText("unc", "&unc;")
        parser.defineEntityReplacementText("v-unspec", "&v-unspec;")
        parser.defineEntityReplacementText("v1", "&v1;")
        parser.defineEntityReplacementText("v1-s", "&v1-s;")
        parser.defineEntityReplacementText("v2a-s", "&v2a-s;")
        parser.defineEntityReplacementText("v2b-k", "&v2b-k;")
        parser.defineEntityReplacementText("v2b-s", "&v2b-s;")
        parser.defineEntityReplacementText("v2d-k", "&v2d-k;")
        parser.defineEntityReplacementText("v2d-s", "&v2d-s;")
        parser.defineEntityReplacementText("v2g-k", "&v2g-k;")
        parser.defineEntityReplacementText("v2g-s", "&v2g-s;")
        parser.defineEntityReplacementText("v2h-k", "&v2h-k;")
        parser.defineEntityReplacementText("v2h-s", "&v2h-s;")
        parser.defineEntityReplacementText("v2k-k", "&v2k-k;")
        parser.defineEntityReplacementText("v2k-s", "&v2k-s;")
        parser.defineEntityReplacementText("v2m-k", "&v2m-k;")
        parser.defineEntityReplacementText("v2m-s", "&v2m-s;")
        parser.defineEntityReplacementText("v2n-s", "&v2n-s;")
        parser.defineEntityReplacementText("v2r-k", "&v2r-k;")
        parser.defineEntityReplacementText("v2r-s", "&v2r-s;")
        parser.defineEntityReplacementText("v2s-s", "&v2s-s;")
        parser.defineEntityReplacementText("v2t-k", "&v2t-k;")
        parser.defineEntityReplacementText("v2t-s", "&v2t-s;")
        parser.defineEntityReplacementText("v2w-s", "&v2w-s;")
        parser.defineEntityReplacementText("v2y-k", "&v2y-k;")
        parser.defineEntityReplacementText("v2y-s", "&v2y-s;")
        parser.defineEntityReplacementText("v2z-s", "&v2z-s;")
        parser.defineEntityReplacementText("v4b", "&v4b;")
        parser.defineEntityReplacementText("v4g", "&v4g;")
        parser.defineEntityReplacementText("v4h", "&v4h;")
        parser.defineEntityReplacementText("v4k", "&v4k;")
        parser.defineEntityReplacementText("v4m", "&v4m;")
        parser.defineEntityReplacementText("v4n", "&v4n;")
        parser.defineEntityReplacementText("v4r", "&v4r;")
        parser.defineEntityReplacementText("v4s", "&v4s;")
        parser.defineEntityReplacementText("v4t", "&v4t;")
        parser.defineEntityReplacementText("v5aru", "&v5aru;")
        parser.defineEntityReplacementText("v5b", "&v5b;")
        parser.defineEntityReplacementText("v5g", "&v5g;")
        parser.defineEntityReplacementText("v5k", "&v5k;")
        parser.defineEntityReplacementText("v5k-s", "&v5k-s;")
        parser.defineEntityReplacementText("v5m", "&v5m;")
        parser.defineEntityReplacementText("v5n", "&v5n;")
        parser.defineEntityReplacementText("v5r", "&v5r;")
        parser.defineEntityReplacementText("v5r-i", "&v5r-i;")
        parser.defineEntityReplacementText("v5s", "&v5s;")
        parser.defineEntityReplacementText("v5t", "&v5t;")
        parser.defineEntityReplacementText("v5u", "&v5u;")
        parser.defineEntityReplacementText("v5u-s", "&v5u-s;")
        parser.defineEntityReplacementText("v5uru", "&v5uru;")
        parser.defineEntityReplacementText("vi", "&vi;")
        parser.defineEntityReplacementText("vk", "&vk;")
        parser.defineEntityReplacementText("vn", "&vn;")
        parser.defineEntityReplacementText("vr", "&vr;")
        parser.defineEntityReplacementText("vs", "&vs;")
        parser.defineEntityReplacementText("vs-c", "&vs-c;")
        parser.defineEntityReplacementText("vs-i", "&vs-i;")
        parser.defineEntityReplacementText("vs-s", "&vs-s;")
        parser.defineEntityReplacementText("vt", "&vt;")
        parser.defineEntityReplacementText("vz", "&vz;")
    }
}