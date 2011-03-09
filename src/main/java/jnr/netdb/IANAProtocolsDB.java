/*
 * Copyright (C) 2010 Wayne Meissner
 *
 * This file is part of jnr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jnr.netdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pre-compiled table of IANA protocol numbers
 */
class IANAProtocolsDB implements ProtocolsDB {

    private final Map<String, Protocol> nameToProto;
    private final Map<Integer, Protocol> numberToProto;

    public static final IANAProtocolsDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        public static final IANAProtocolsDB INSTANCE = initProtocolDB().build();
    }

    private IANAProtocolsDB(Map<String, Protocol> nameToProto, Map<Integer, Protocol> numberToProto) {
        this.nameToProto = nameToProto;
        this.numberToProto = numberToProto;
    }
    
    public Protocol getProtocolByName(String name) {
        return nameToProto.get(name);
    }

    public Protocol getProtocolByNumber(Integer number) {
        return numberToProto.get(number);
    }

    public Collection<Protocol> getAllProtocols() {
        return Collections.unmodifiableCollection(nameToProto.values());
    }

    private static final class ProtocolDBBuilder {
        private static final List<String> emptyAliases = Collections.emptyList();
        private final Map<String, Protocol> nameToProto = new HashMap<String, Protocol>();
        private final Map<Integer, Protocol> numberToProto = new HashMap<Integer, Protocol>();

        public void add(String name, int proto, String... aliases) {
            List<String> aliasesList = aliases.length > 0 ? new ArrayList<String>(Arrays.asList(aliases)) : emptyAliases;
            
            Protocol p = new Protocol(name, proto, aliasesList);
            nameToProto.put(name, p);
            for (String alias : aliases) {
                nameToProto.put(alias, p);
            }
            
            numberToProto.put(proto, p);
        }
        
        public IANAProtocolsDB build() {
            return new IANAProtocolsDB(nameToProto, numberToProto);
        }
    }

    private static final ProtocolDBBuilder initProtocolDB() {
        ProtocolDBBuilder builder = new ProtocolDBBuilder();

        builder.add("ip", 0, "IP");
        builder.add("icmp", 1, "ICMP");
        builder.add("igmp", 2, "IGMP");
        builder.add("ggp", 3, "GGP");
        builder.add("ipencap", 4, "IP-ENCAP");
        builder.add("st2", 5, "ST2");
        builder.add("tcp", 6, "TCP");
        builder.add("cbt", 7, "CBT");
        builder.add("egp", 8, "EGP");
        builder.add("igp", 9, "IGP");
        builder.add("bbn-rcc", 10, "BBN-RCC-MON");
        builder.add("nvp", 11, "NVP-II");
        builder.add("pup", 12, "PUP");
        builder.add("argus", 13, "ARGUS");
        builder.add("emcon", 14, "EMCON");
        builder.add("xnet", 15, "XNET");
        builder.add("chaos", 16, "CHAOS");
        builder.add("udp", 17, "UDP");
        builder.add("mux", 18, "MUX");
        builder.add("dcn", 19, "DCN-MEAS");
        builder.add("hmp", 20, "HMP");
        builder.add("prm", 21, "PRM");
        builder.add("xns-idp", 22, "XNS-IDP");
        builder.add("trunk-1", 23, "TRUNK-1");
        builder.add("trunk-2", 24, "TRUNK-2");
        builder.add("leaf-1", 25, "LEAF-1");
        builder.add("leaf-2", 26, "LEAF-2");
        builder.add("rdp", 27, "RDP");
        builder.add("irtp", 28, "IRTP");
        builder.add("iso-tp4", 29, "ISO-TP4");
        builder.add("netblt", 30, "NETBLT");
        builder.add("mfe-nsp", 31, "MFE-NSP");
        builder.add("merit-inp", 32, "MERIT-INP");
        builder.add("sep", 33, "SEP");
        builder.add("3pc", 34, "3PC");
        builder.add("idpr", 35, "IDPR");
        builder.add("xtp", 36, "XTP");
        builder.add("ddp", 37, "DDP");
        builder.add("idpr-cmtp", 38, "IDPR-CMTP");
        builder.add("tp++", 39, "TP++");
        builder.add("il", 40, "IL");
        builder.add("ipv6", 41, "IPV6");
        builder.add("sdrp", 42, "SDRP");
        builder.add("ipv6-route", 43, "IPV6-ROUTE");
        builder.add("ipv6-frag", 44, "IPV6-FRAG");
        builder.add("idrp", 45, "IDRP");
        builder.add("rsvp", 46, "RSVP");
        builder.add("gre", 47, "GRE");
        builder.add("mhrp", 48, "MHRP");
        builder.add("bna", 49, "BNA");
        builder.add("esp", 50, "ESP");
        builder.add("ah", 51, "AH");
        builder.add("i-nlsp", 52, "I-NLSP");
        builder.add("swipe", 53, "SWIPE");
        builder.add("narp", 54, "NARP");
        builder.add("mobile", 55, "MOBILE");
        builder.add("tlsp", 56, "TLSP");
        builder.add("skip", 57, "SKIP");
        builder.add("ipv6-icmp", 58, "IPV6-ICMP");
        builder.add("ipv6-nonxt", 59, "IPV6-NONXT");
        builder.add("ipv6-opts", 60, "IPV6-OPTS");
        builder.add("cftp", 62, "CFTP");
        builder.add("sat-expak", 64, "SAT-EXPAK");
        builder.add("kryptolan", 65, "KRYPTOLAN");
        builder.add("rvd", 66, "RVD");
        builder.add("ippc", 67, "IPPC");
        builder.add("sat-mon", 69, "SAT-MON");
        builder.add("visa", 70, "VISA");
        builder.add("ipcv", 71, "IPCV");
        builder.add("cpnx", 72, "CPNX");
        builder.add("cphb", 73, "CPHB");
        builder.add("wsn", 74, "WSN");
        builder.add("pvp", 75, "PVP");
        builder.add("br-sat-mon", 76, "BR-SAT-MON");
        builder.add("sun-nd", 77, "SUN-ND");
        builder.add("wb-mon", 78, "WB-MON");
        builder.add("wb-expak", 79, "WB-EXPAK");
        builder.add("iso-ip", 80, "ISO-IP");
        builder.add("vmtp", 81, "VMTP");
        builder.add("secure-vmtp", 82, "SECURE-VMTP");
        builder.add("vines", 83, "VINES");
        builder.add("ttp", 84, "TTP");
        builder.add("nsfnet-igp", 85, "NSFNET-IGP");
        builder.add("dgp", 86, "DGP");
        builder.add("tcf", 87, "TCF");
        builder.add("eigrp", 88, "EIGRP");
        builder.add("ospf", 89, "OSPFIGP");
        builder.add("sprite-rpc", 90, "Sprite-RPC");
        builder.add("larp", 91, "LARP");
        builder.add("mtp", 92, "MTP");
        builder.add("ax.25", 93, "AX.25");
        builder.add("ipip", 94, "IPIP");
        builder.add("micp", 95, "MICP");
        builder.add("scc-sp", 96, "SCC-SP");
        builder.add("etherip", 97, "ETHERIP");
        builder.add("encap", 98, "ENCAP");
        builder.add("gmtp", 100, "GMTP");
        builder.add("ifmp", 101, "IFMP");
        builder.add("pnni", 102, "PNNI");
        builder.add("pim", 103, "PIM");
        builder.add("aris", 104, "ARIS");
        builder.add("scps", 105, "SCPS");
        builder.add("qnx", 106, "QNX");
        builder.add("a/n", 107, "A/N");
        builder.add("ipcomp", 108, "IPComp");
        builder.add("snp", 109, "SNP");
        builder.add("compaq-peer", 110, "Compaq-Peer");
        builder.add("ipx-in-ip", 111, "IPX-in-IP");
        builder.add("vrrp", 112, "VRRP");
        builder.add("pgm", 113, "PGM");
        builder.add("l2tp", 115, "L2TP");
        builder.add("ddx", 116, "DDX");
        builder.add("iatp", 117, "IATP");
        builder.add("st", 118, "ST");
        builder.add("srp", 119, "SRP");
        builder.add("uti", 120, "UTI");
        builder.add("smp", 121, "SMP");
        builder.add("sm", 122, "SM");
        builder.add("ptp", 123, "PTP");
        builder.add("isis", 124, "ISIS");
        builder.add("fire", 125, "FIRE");
        builder.add("crtp", 126, "CRTP");
        builder.add("crdup", 127, "CRUDP");
        builder.add("sscopmce", 128, "SSCOPMCE");
        builder.add("iplt", 129, "IPLT");
        builder.add("sps", 130, "SPS");
        builder.add("pipe", 131, "PIPE");
        builder.add("sctp", 132, "SCTP");
        builder.add("fc", 133, "FC");
        builder.add("divert", 254, "DIVERT");


        return builder;
    }
}
