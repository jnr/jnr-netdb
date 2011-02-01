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
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

final class IANAServicesDB implements ServicesDB {
    private final Map<Integer, Service> tcpPortToService;
    private final Map<Integer, Service> udpPortToService;
    private final Map<String, Service> tcpNameToService;
    private final Map<String, Service> udpNameToService;
    private final List<Service> allServices;
    
    private IANAServicesDB(Map<String, Service> tcpNameToService, Map<String, Service> udpNameToService,
            Map<Integer, Service> tcpServices, Map<Integer, Service> udpServices) {
        this.tcpNameToService = tcpNameToService;
        this.udpNameToService = udpNameToService;
        this.tcpPortToService = tcpServices;
        this.udpPortToService = udpServices;

        List<Service> services = new ArrayList<Service>(tcpNameToService.size() + udpNameToService.size());
        services.addAll(tcpNameToService.values());
        services.addAll(udpNameToService.values());
        this.allServices = Collections.unmodifiableList(services);
    }

    private static final class SingletonHolder {
        public static final IANAServicesDB INSTANCE = buildServices();
    }

    public static final IANAServicesDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public final Service getServiceByName(String name, String proto) {
        if ("tcp".equals(proto)) {
            return tcpNameToService.get(name);

        } else if ("udp".equals(proto)) {
            return udpNameToService.get(name);

        } else if (proto == null) {
            // a proto of null matches any service protocol
            Service s = tcpNameToService.get(name);
            return s != null ? s : udpNameToService.get(name);

        } else {
            return null;
        }
    }

    
    public final Service getServiceByPort(Integer port, String proto) {
        if ("tcp".equals(proto)) {
            return tcpPortToService.get(port);

        } else if ("udp".equals(proto)) {
            return udpPortToService.get(port);

        } else if (proto == null) {
            // a proto of null matches any service protocol
            Service s = tcpPortToService.get(port);
            return s != null ? s : udpPortToService.get(port);

        } else {
            return null;
        }
    }

    public final List<Service> getAllServices() {
        return allServices;
    }

    private static final class ServicesBuilder {
        private static final List<String> emptyAliases = Collections.emptyList();
        final Map<String, Service> tcpNameToService = new HashMap<String, Service>();
        final Map<String, Service> udpNameToService = new HashMap<String, Service>();
        final Map<Integer, Service> tcpPortToService = new HashMap<Integer, Service>();
        final Map<Integer, Service> udpPortToService = new HashMap<Integer, Service>();

        public final void add(String service, int port) {
            String[] descr = service.split("/");

            add(descr[0], descr[1], port);
        }

        public final void add(String service, String proto, int port) {

            Service s = new Service(service, port, proto, emptyAliases);
            
            if ("tcp".equals(proto)) {
                tcpNameToService.put(service, s);
                tcpPortToService.put(port, s);

            } else if ("udp".equals(proto)) {
                udpNameToService.put(service, s);
                udpPortToService.put(port, s);
            }
        }

        IANAServicesDB build() {
            return new IANAServicesDB(tcpNameToService, udpNameToService, tcpPortToService, udpPortToService);
        }
    }
    
    private static final IANAServicesDB buildServices() {
        
        ServicesBuilder builder = new ServicesBuilder();

        builder.add("spr-itunes", "tcp", 0);
        
        builder.add("spl-itunes", "tcp", 0);
        
        builder.add("tcpmux", "tcp", 1);
        
        builder.add("tcpmux", "udp", 1);
        
        builder.add("compressnet", "tcp", 2);
        
        builder.add("compressnet", "udp", 2);
        
        builder.add("compressnet", "tcp", 3);
        
        builder.add("compressnet", "udp", 3);
        
        builder.add("rje", "tcp", 5);
        
        builder.add("rje", "udp", 5);
        
        builder.add("echo", "tcp", 7);
        
        builder.add("echo", "udp", 7);
        
        builder.add("discard", "tcp", 9);
        
        builder.add("discard", "udp", 9);
        
        builder.add("discard/sctp", 9);
        
        builder.add("discard/dccp", 9);
        
        builder.add("systat", "tcp", 11);
        
        builder.add("systat", "udp", 11);
        
        builder.add("daytime", "tcp", 13);
        
        builder.add("daytime", "udp", 13);
        
        builder.add("qotd", "tcp", 17);
        
        builder.add("qotd", "udp", 17);
        
        builder.add("msp", "tcp", 18);
        
        builder.add("msp", "udp", 18);
        
        builder.add("chargen", "tcp", 19);
        
        builder.add("chargen", "udp", 19);
        
        builder.add("ftp-data", "tcp", 20);
        
        builder.add("ftp-data", "udp", 20);
        
        builder.add("ftp-data/sctp", 20);
        
        builder.add("ftp", "tcp", 21);
        
        builder.add("ftp", "udp", 21);
        
        builder.add("ftp/sctp", 21);
        
        builder.add("ssh", "tcp", 22);
        
        builder.add("ssh", "udp", 22);
        
        builder.add("ssh/sctp", 22);
        
        builder.add("telnet", "tcp", 23);
        
        builder.add("telnet", "udp", 23);
        
        builder.add("smtp", "tcp", 25);
        
        builder.add("smtp", "udp", 25);
        
        builder.add("nsw-fe", "tcp", 27);
        
        builder.add("nsw-fe", "udp", 27);
        
        builder.add("msg-icp", "tcp", 29);
        
        builder.add("msg-icp", "udp", 29);
        
        builder.add("msg-auth", "tcp", 31);
        
        builder.add("msg-auth", "udp", 31);
        
        builder.add("dsp", "tcp", 33);
        
        builder.add("dsp", "udp", 33);
        
        builder.add("time", "tcp", 37);
        
        builder.add("time", "udp", 37);
        
        builder.add("rap", "tcp", 38);
        
        builder.add("rap", "udp", 38);
        
        builder.add("rlp", "tcp", 39);
        
        builder.add("rlp", "udp", 39);
        
        builder.add("graphics", "tcp", 41);
        
        builder.add("graphics", "udp", 41);
        
        builder.add("name", "tcp", 42);
        
        builder.add("name", "udp", 42);
        
        builder.add("nameserver", "tcp", 42);
        
        builder.add("nameserver", "udp", 42);
        
        builder.add("nicname", "tcp", 43);
        
        builder.add("nicname", "udp", 43);
        
        builder.add("mpm-flags", "tcp", 44);
        
        builder.add("mpm-flags", "udp", 44);
        
        builder.add("mpm", "tcp", 45);
        
        builder.add("mpm", "udp", 45);
        
        builder.add("mpm-snd", "tcp", 46);
        
        builder.add("mpm-snd", "udp", 46);
        
        builder.add("ni-ftp", "tcp", 47);
        
        builder.add("ni-ftp", "udp", 47);
        
        builder.add("auditd", "tcp", 48);
        
        builder.add("auditd", "udp", 48);
        
        builder.add("tacacs", "tcp", 49);
        
        builder.add("tacacs", "udp", 49);
        
        builder.add("re-mail-ck", "tcp", 50);
        
        builder.add("re-mail-ck", "udp", 50);
        
        builder.add("la-maint", "tcp", 51);
        
        builder.add("la-maint", "udp", 51);
        
        builder.add("xns-time", "tcp", 52);
        
        builder.add("xns-time", "udp", 52);
        
        builder.add("domain", "tcp", 53);
        
        builder.add("domain", "udp", 53);
        
        builder.add("xns-ch", "tcp", 54);
        
        builder.add("xns-ch", "udp", 54);
        
        builder.add("isi-gl", "tcp", 55);
        
        builder.add("isi-gl", "udp", 55);
        
        builder.add("xns-auth", "tcp", 56);
        
        builder.add("xns-auth", "udp", 56);
        
        builder.add("xns-mail", "tcp", 58);
        
        builder.add("xns-mail", "udp", 58);
        
        builder.add("ni-mail", "tcp", 61);
        
        builder.add("ni-mail", "udp", 61);
        
        builder.add("acas", "tcp", 62);
        
        builder.add("acas", "udp", 62);
        
        builder.add("whois++", "tcp", 63);
        
        builder.add("whois++", "udp", 63);
        
        builder.add("covia", "tcp", 64);
        
        builder.add("covia", "udp", 64);
        
        builder.add("tacacs-ds", "tcp", 65);
        
        builder.add("tacacs-ds", "udp", 65);
        
        builder.add("sql*net", "tcp", 66);
        
        builder.add("sql*net", "udp", 66);
        
        builder.add("bootps", "tcp", 67);
        
        builder.add("bootps", "udp", 67);
        
        builder.add("bootpc", "tcp", 68);
        
        builder.add("bootpc", "udp", 68);
        
        builder.add("tftp", "tcp", 69);
        
        builder.add("tftp", "udp", 69);
        
        builder.add("gopher", "tcp", 70);
        
        builder.add("gopher", "udp", 70);
        
        builder.add("netrjs-1", "tcp", 71);
        
        builder.add("netrjs-1", "udp", 71);
        
        builder.add("netrjs-2", "tcp", 72);
        
        builder.add("netrjs-2", "udp", 72);
        
        builder.add("netrjs-3", "tcp", 73);
        
        builder.add("netrjs-3", "udp", 73);
        
        builder.add("netrjs-4", "tcp", 74);
        
        builder.add("netrjs-4", "udp", 74);
        
        builder.add("deos", "tcp", 76);
        
        builder.add("deos", "udp", 76);
        
        builder.add("vettcp", "tcp", 78);
        
        builder.add("vettcp", "udp", 78);
        
        builder.add("finger", "tcp", 79);
        
        builder.add("finger", "udp", 79);
        
        builder.add("http", "tcp", 80);
        
        builder.add("http", "udp", 80);
        
        builder.add("www", "tcp", 80);
        
        builder.add("www", "udp", 80);
        
        builder.add("www-http", "tcp", 80);
        
        builder.add("www-http", "udp", 80);
        
        builder.add("http/sctp", 80);
        
        builder.add("xfer", "tcp", 82);
        
        builder.add("xfer", "udp", 82);
        
        builder.add("mit-ml-dev", "tcp", 83);
        
        builder.add("mit-ml-dev", "udp", 83);
        
        builder.add("ctf", "tcp", 84);
        
        builder.add("ctf", "udp", 84);
        
        builder.add("mit-ml-dev", "tcp", 85);
        
        builder.add("mit-ml-dev", "udp", 85);
        
        builder.add("mfcobol", "tcp", 86);
        
        builder.add("mfcobol", "udp", 86);
        
        builder.add("kerberos", "tcp", 88);
        
        builder.add("kerberos", "udp", 88);
        
        builder.add("su-mit-tg", "tcp", 89);
        
        builder.add("su-mit-tg", "udp", 89);
        
        builder.add("dnsix", "tcp", 90);
        
        builder.add("dnsix", "udp", 90);
        
        builder.add("mit-dov", "tcp", 91);
        
        builder.add("mit-dov", "udp", 91);
        
        builder.add("npp", "tcp", 92);
        
        builder.add("npp", "udp", 92);
        
        builder.add("dcp", "tcp", 93);
        
        builder.add("dcp", "udp", 93);
        
        builder.add("objcall", "tcp", 94);
        
        builder.add("objcall", "udp", 94);
        
        builder.add("supdup", "tcp", 95);
        
        builder.add("supdup", "udp", 95);
        
        builder.add("dixie", "tcp", 96);
        
        builder.add("dixie", "udp", 96);
        
        builder.add("swift-rvf", "tcp", 97);
        
        builder.add("swift-rvf", "udp", 97);
        
        builder.add("tacnews", "tcp", 98);
        
        builder.add("tacnews", "udp", 98);
        
        builder.add("metagram", "tcp", 99);
        
        builder.add("metagram", "udp", 99);
        
        builder.add("newacct", "tcp", 100);
        
        builder.add("hostname", "tcp", 101);
        
        builder.add("hostname", "udp", 101);
        
        builder.add("iso-tsap", "tcp", 102);
        
        builder.add("iso-tsap", "udp", 102);
        
        builder.add("gppitnp", "tcp", 103);
        
        builder.add("gppitnp", "udp", 103);
        
        builder.add("acr-nema", "tcp", 104);
        
        builder.add("acr-nema", "udp", 104);
        
        builder.add("cso", "tcp", 105);
        
        builder.add("cso", "udp", 105);
        
        builder.add("csnet-ns", "tcp", 105);
        
        builder.add("csnet-ns", "udp", 105);
        
        builder.add("3com-tsmux", "tcp", 106);
        
        builder.add("3com-tsmux", "udp", 106);
        
        builder.add("rtelnet", "tcp", 107);
        
        builder.add("rtelnet", "udp", 107);
        
        builder.add("snagas", "tcp", 108);
        
        builder.add("snagas", "udp", 108);
        
        builder.add("pop2", "tcp", 109);
        
        builder.add("pop2", "udp", 109);
        
        builder.add("pop3", "tcp", 110);
        
        builder.add("pop3", "udp", 110);
        
        builder.add("sunrpc", "tcp", 111);
        
        builder.add("sunrpc", "udp", 111);
        
        builder.add("mcidas", "tcp", 112);
        
        builder.add("mcidas", "udp", 112);
        
        builder.add("ident", "tcp", 113);
        
        builder.add("auth", "tcp", 113);
        
        builder.add("auth", "udp", 113);
        
        builder.add("sftp", "tcp", 115);
        
        builder.add("sftp", "udp", 115);
        
        builder.add("ansanotify", "tcp", 116);
        
        builder.add("ansanotify", "udp", 116);
        
        builder.add("uucp-path", "tcp", 117);
        
        builder.add("uucp-path", "udp", 117);
        
        builder.add("sqlserv", "tcp", 118);
        
        builder.add("sqlserv", "udp", 118);
        
        builder.add("nntp", "tcp", 119);
        
        builder.add("nntp", "udp", 119);
        
        builder.add("cfdptkt", "tcp", 120);
        
        builder.add("cfdptkt", "udp", 120);
        
        builder.add("erpc", "tcp", 121);
        
        builder.add("erpc", "udp", 121);
        
        builder.add("smakynet", "tcp", 122);
        
        builder.add("smakynet", "udp", 122);
        
        builder.add("ntp", "tcp", 123);
        
        builder.add("ntp", "udp", 123);
        
        builder.add("ansatrader", "tcp", 124);
        
        builder.add("ansatrader", "udp", 124);
        
        builder.add("locus-map", "tcp", 125);
        
        builder.add("locus-map", "udp", 125);
        
        builder.add("nxedit", "tcp", 126);
        
        builder.add("nxedit", "udp", 126);
        
        builder.add("locus-con", "tcp", 127);
        
        builder.add("locus-con", "udp", 127);
        
        builder.add("gss-xlicen", "tcp", 128);
        
        builder.add("gss-xlicen", "udp", 128);
        
        builder.add("pwdgen", "tcp", 129);
        
        builder.add("pwdgen", "udp", 129);
        
        builder.add("cisco-fna", "tcp", 130);
        
        builder.add("cisco-fna", "udp", 130);
        
        builder.add("cisco-tna", "tcp", 131);
        
        builder.add("cisco-tna", "udp", 131);
        
        builder.add("cisco-sys", "tcp", 132);
        
        builder.add("cisco-sys", "udp", 132);
        
        builder.add("statsrv", "tcp", 133);
        
        builder.add("statsrv", "udp", 133);
        
        builder.add("ingres-net", "tcp", 134);
        
        builder.add("ingres-net", "udp", 134);
        
        builder.add("epmap", "tcp", 135);
        
        builder.add("epmap", "udp", 135);
        
        builder.add("profile", "tcp", 136);
        
        builder.add("profile", "udp", 136);
        
        builder.add("netbios-ns", "tcp", 137);
        
        builder.add("netbios-ns", "udp", 137);
        
        builder.add("netbios-dgm", "tcp", 138);
        
        builder.add("netbios-dgm", "udp", 138);
        
        builder.add("netbios-ssn", "tcp", 139);
        
        builder.add("netbios-ssn", "udp", 139);
        
        builder.add("emfis-data", "tcp", 140);
        
        builder.add("emfis-data", "udp", 140);
        
        builder.add("emfis-cntl", "tcp", 141);
        
        builder.add("emfis-cntl", "udp", 141);
        
        builder.add("bl-idm", "tcp", 142);
        
        builder.add("bl-idm", "udp", 142);
        
        builder.add("imap", "tcp", 143);
        
        builder.add("imap", "udp", 143);
        
        builder.add("uma", "tcp", 144);
        
        builder.add("uma", "udp", 144);
        
        builder.add("uaac", "tcp", 145);
        
        builder.add("uaac", "udp", 145);
        
        builder.add("iso-tp0", "tcp", 146);
        
        builder.add("iso-tp0", "udp", 146);
        
        builder.add("iso-ip", "tcp", 147);
        
        builder.add("iso-ip", "udp", 147);
        
        builder.add("jargon", "tcp", 148);
        
        builder.add("jargon", "udp", 148);
        
        builder.add("aed-512", "tcp", 149);
        
        builder.add("aed-512", "udp", 149);
        
        builder.add("sql-net", "tcp", 150);
        
        builder.add("sql-net", "udp", 150);
        
        builder.add("hems", "tcp", 151);
        
        builder.add("hems", "udp", 151);
        
        builder.add("bftp", "tcp", 152);
        
        builder.add("bftp", "udp", 152);
        
        builder.add("sgmp", "tcp", 153);
        
        builder.add("sgmp", "udp", 153);
        
        builder.add("netsc-prod", "tcp", 154);
        
        builder.add("netsc-prod", "udp", 154);
        
        builder.add("netsc-dev", "tcp", 155);
        
        builder.add("netsc-dev", "udp", 155);
        
        builder.add("sqlsrv", "tcp", 156);
        
        builder.add("sqlsrv", "udp", 156);
        
        builder.add("knet-cmp", "tcp", 157);
        
        builder.add("knet-cmp", "udp", 157);
        
        builder.add("pcmail-srv", "tcp", 158);
        
        builder.add("pcmail-srv", "udp", 158);
        
        builder.add("nss-routing", "tcp", 159);
        
        builder.add("nss-routing", "udp", 159);
        
        builder.add("sgmp-traps", "tcp", 160);
        
        builder.add("sgmp-traps", "udp", 160);
        
        builder.add("snmp", "tcp", 161);
        
        builder.add("snmp", "udp", 161);
        
        builder.add("snmptrap", "tcp", 162);
        
        builder.add("snmptrap", "udp", 162);
        
        builder.add("cmip-man", "tcp", 163);
        
        builder.add("cmip-man", "udp", 163);
        
        builder.add("cmip-agent", "tcp", 164);
        
        builder.add("cmip-agent", "udp", 164);
        
        builder.add("xns-courier", "tcp", 165);
        
        builder.add("xns-courier", "udp", 165);
        
        builder.add("s-net", "tcp", 166);
        
        builder.add("s-net", "udp", 166);
        
        builder.add("namp", "tcp", 167);
        
        builder.add("namp", "udp", 167);
        
        builder.add("rsvd", "tcp", 168);
        
        builder.add("rsvd", "udp", 168);
        
        builder.add("send", "tcp", 169);
        
        builder.add("send", "udp", 169);
        
        builder.add("print-srv", "tcp", 170);
        
        builder.add("print-srv", "udp", 170);
        
        builder.add("multiplex", "tcp", 171);
        
        builder.add("multiplex", "udp", 171);
        
        builder.add("cl/1", "tcp", 172);
        
        builder.add("cl/1", "udp", 172);
        
        builder.add("xyplex-mux", "tcp", 173);
        
        builder.add("xyplex-mux", "udp", 173);
        
        builder.add("mailq", "tcp", 174);
        
        builder.add("mailq", "udp", 174);
        
        builder.add("vmnet", "tcp", 175);
        
        builder.add("vmnet", "udp", 175);
        
        builder.add("genrad-mux", "tcp", 176);
        
        builder.add("genrad-mux", "udp", 176);
        
        builder.add("xdmcp", "tcp", 177);
        
        builder.add("xdmcp", "udp", 177);
        
        builder.add("nextstep", "tcp", 178);
        
        builder.add("nextstep", "udp", 178);
        
        builder.add("bgp", "tcp", 179);
        
        builder.add("bgp", "udp", 179);
        
        builder.add("bgp/sctp", 179);
        
        builder.add("ris", "tcp", 180);
        
        builder.add("ris", "udp", 180);
        
        builder.add("unify", "tcp", 181);
        
        builder.add("unify", "udp", 181);
        
        builder.add("audit", "tcp", 182);
        
        builder.add("audit", "udp", 182);
        
        builder.add("ocbinder", "tcp", 183);
        
        builder.add("ocbinder", "udp", 183);
        
        builder.add("ocserver", "tcp", 184);
        
        builder.add("ocserver", "udp", 184);
        
        builder.add("remote-kis", "tcp", 185);
        
        builder.add("remote-kis", "udp", 185);
        
        builder.add("kis", "tcp", 186);
        
        builder.add("kis", "udp", 186);
        
        builder.add("aci", "tcp", 187);
        
        builder.add("aci", "udp", 187);
        
        builder.add("mumps", "tcp", 188);
        
        builder.add("mumps", "udp", 188);
        
        builder.add("qft", "tcp", 189);
        
        builder.add("qft", "udp", 189);
        
        builder.add("gacp", "tcp", 190);
        
        builder.add("gacp", "udp", 190);
        
        builder.add("prospero", "tcp", 191);
        
        builder.add("prospero", "udp", 191);
        
        builder.add("osu-nms", "tcp", 192);
        
        builder.add("osu-nms", "udp", 192);
        
        builder.add("srmp", "tcp", 193);
        
        builder.add("srmp", "udp", 193);
        
        builder.add("irc", "tcp", 194);
        
        builder.add("irc", "udp", 194);
        
        builder.add("dn6-nlm-aud", "tcp", 195);
        
        builder.add("dn6-nlm-aud", "udp", 195);
        
        builder.add("dn6-smm-red", "tcp", 196);
        
        builder.add("dn6-smm-red", "udp", 196);
        
        builder.add("dls", "tcp", 197);
        
        builder.add("dls", "udp", 197);
        
        builder.add("dls-mon", "tcp", 198);
        
        builder.add("dls-mon", "udp", 198);
        
        builder.add("smux", "tcp", 199);
        
        builder.add("smux", "udp", 199);
        
        builder.add("src", "tcp", 200);
        
        builder.add("src", "udp", 200);
        
        builder.add("at-rtmp", "tcp", 201);
        
        builder.add("at-rtmp", "udp", 201);
        
        builder.add("at-nbp", "tcp", 202);
        
        builder.add("at-nbp", "udp", 202);
        
        builder.add("at-3", "tcp", 203);
        
        builder.add("at-3", "udp", 203);
        
        builder.add("at-echo", "tcp", 204);
        
        builder.add("at-echo", "udp", 204);
        
        builder.add("at-5", "tcp", 205);
        
        builder.add("at-5", "udp", 205);
        
        builder.add("at-zis", "tcp", 206);
        
        builder.add("at-zis", "udp", 206);
        
        builder.add("at-7", "tcp", 207);
        
        builder.add("at-7", "udp", 207);
        
        builder.add("at-8", "tcp", 208);
        
        builder.add("at-8", "udp", 208);
        
        builder.add("qmtp", "tcp", 209);
        
        builder.add("qmtp", "udp", 209);
        
        builder.add("z39.50", "tcp", 210);
        
        builder.add("z39.50", "udp", 210);
        
        builder.add("914c/g", "tcp", 211);
        
        builder.add("914c/g", "udp", 211);
        
        builder.add("anet", "tcp", 212);
        
        builder.add("anet", "udp", 212);
        
        builder.add("ipx", "tcp", 213);
        
        builder.add("ipx", "udp", 213);
        
        builder.add("vmpwscs", "tcp", 214);
        
        builder.add("vmpwscs", "udp", 214);
        
        builder.add("softpc", "tcp", 215);
        
        builder.add("softpc", "udp", 215);
        
        builder.add("CAIlic", "tcp", 216);
        
        builder.add("CAIlic", "udp", 216);
        
        builder.add("dbase", "tcp", 217);
        
        builder.add("dbase", "udp", 217);
        
        builder.add("mpp", "tcp", 218);
        
        builder.add("mpp", "udp", 218);
        
        builder.add("uarps", "tcp", 219);
        
        builder.add("uarps", "udp", 219);
        
        builder.add("imap3", "tcp", 220);
        
        builder.add("imap3", "udp", 220);
        
        builder.add("fln-spx", "tcp", 221);
        
        builder.add("fln-spx", "udp", 221);
        
        builder.add("rsh-spx", "tcp", 222);
        
        builder.add("rsh-spx", "udp", 222);
        
        builder.add("cdc", "tcp", 223);
        
        builder.add("cdc", "udp", 223);
        
        builder.add("masqdialer", "tcp", 224);
        
        builder.add("masqdialer", "udp", 224);
        
        builder.add("direct", "tcp", 242);
        
        builder.add("direct", "udp", 242);
        
        builder.add("sur-meas", "tcp", 243);
        
        builder.add("sur-meas", "udp", 243);
        
        builder.add("inbusiness", "tcp", 244);
        
        builder.add("inbusiness", "udp", 244);
        
        builder.add("link", "tcp", 245);
        
        builder.add("link", "udp", 245);
        
        builder.add("dsp3270", "tcp", 246);
        
        builder.add("dsp3270", "udp", 246);
        
        builder.add("subntbcst_tftp", "tcp", 247);
        
        builder.add("subntbcst_tftp", "udp", 247);
        
        builder.add("bhfhs", "tcp", 248);
        
        builder.add("bhfhs", "udp", 248);
        
        builder.add("rap", "tcp", 256);
        
        builder.add("rap", "udp", 256);
        
        builder.add("set", "tcp", 257);
        
        builder.add("set", "udp", 257);
        
        builder.add("esro-gen", "tcp", 259);
        
        builder.add("esro-gen", "udp", 259);
        
        builder.add("openport", "tcp", 260);
        
        builder.add("openport", "udp", 260);
        
        builder.add("nsiiops", "tcp", 261);
        
        builder.add("nsiiops", "udp", 261);
        
        builder.add("arcisdms", "tcp", 262);
        
        builder.add("arcisdms", "udp", 262);
        
        builder.add("hdap", "tcp", 263);
        
        builder.add("hdap", "udp", 263);
        
        builder.add("bgmp", "tcp", 264);
        
        builder.add("bgmp", "udp", 264);
        
        builder.add("x-bone-ctl", "tcp", 265);
        
        builder.add("x-bone-ctl", "udp", 265);
        
        builder.add("sst", "tcp", 266);
        
        builder.add("sst", "udp", 266);
        
        builder.add("td-service", "tcp", 267);
        
        builder.add("td-service", "udp", 267);
        
        builder.add("td-replica", "tcp", 268);
        
        builder.add("td-replica", "udp", 268);
        
        builder.add("manet", "tcp", 269);
        
        builder.add("manet", "udp", 269);
        
        builder.add("http-mgmt", "tcp", 280);
        
        builder.add("http-mgmt", "udp", 280);
        
        builder.add("personal-link", "tcp", 281);
        
        builder.add("personal-link", "udp", 281);
        
        builder.add("cableport-ax", "tcp", 282);
        
        builder.add("cableport-ax", "udp", 282);
        
        builder.add("rescap", "tcp", 283);
        
        builder.add("rescap", "udp", 283);
        
        builder.add("corerjd", "tcp", 284);
        
        builder.add("corerjd", "udp", 284);
        
        builder.add("fxp", "tcp", 286);
        
        builder.add("fxp", "udp", 286);
        
        builder.add("k-block", "tcp", 287);
        
        builder.add("k-block", "udp", 287);
        
        builder.add("novastorbakcup", "tcp", 308);
        
        builder.add("novastorbakcup", "udp", 308);
        
        builder.add("entrusttime", "tcp", 309);
        
        builder.add("entrusttime", "udp", 309);
        
        builder.add("bhmds", "tcp", 310);
        
        builder.add("bhmds", "udp", 310);
        
        builder.add("asip-webadmin", "tcp", 311);
        
        builder.add("asip-webadmin", "udp", 311);
        
        builder.add("vslmp", "tcp", 312);
        
        builder.add("vslmp", "udp", 312);
        
        builder.add("magenta-logic", "tcp", 313);
        
        builder.add("magenta-logic", "udp", 313);
        
        builder.add("opalis-robot", "tcp", 314);
        
        builder.add("opalis-robot", "udp", 314);
        
        builder.add("dpsi", "tcp", 315);
        
        builder.add("dpsi", "udp", 315);
        
        builder.add("decauth", "tcp", 316);
        
        builder.add("decauth", "udp", 316);
        
        builder.add("zannet", "tcp", 317);
        
        builder.add("zannet", "udp", 317);
        
        builder.add("pkix-timestamp", "tcp", 318);
        
        builder.add("pkix-timestamp", "udp", 318);
        
        builder.add("ptp-event", "tcp", 319);
        
        builder.add("ptp-event", "udp", 319);
        
        builder.add("ptp-general", "tcp", 320);
        
        builder.add("ptp-general", "udp", 320);
        
        builder.add("pip", "tcp", 321);
        
        builder.add("pip", "udp", 321);
        
        builder.add("rtsps", "tcp", 322);
        
        builder.add("rtsps", "udp", 322);
        
        builder.add("texar", "tcp", 333);
        
        builder.add("texar", "udp", 333);
        
        builder.add("pdap", "tcp", 344);
        
        builder.add("pdap", "udp", 344);
        
        builder.add("pawserv", "tcp", 345);
        
        builder.add("pawserv", "udp", 345);
        
        builder.add("zserv", "tcp", 346);
        
        builder.add("zserv", "udp", 346);
        
        builder.add("fatserv", "tcp", 347);
        
        builder.add("fatserv", "udp", 347);
        
        builder.add("csi-sgwp", "tcp", 348);
        
        builder.add("csi-sgwp", "udp", 348);
        
        builder.add("mftp", "tcp", 349);
        
        builder.add("mftp", "udp", 349);
        
        builder.add("matip-type-a", "tcp", 350);
        
        builder.add("matip-type-a", "udp", 350);
        
        builder.add("matip-type-b", "tcp", 351);
        
        builder.add("matip-type-b", "udp", 351);
        
        builder.add("bhoetty", "tcp", 351);
        
        builder.add("bhoetty", "udp", 351);
        
        builder.add("dtag-ste-sb", "tcp", 352);
        
        builder.add("dtag-ste-sb", "udp", 352);
        
        builder.add("bhoedap4", "tcp", 352);
        
        builder.add("bhoedap4", "udp", 352);
        
        builder.add("ndsauth", "tcp", 353);
        
        builder.add("ndsauth", "udp", 353);
        
        builder.add("bh611", "tcp", 354);
        
        builder.add("bh611", "udp", 354);
        
        builder.add("datex-asn", "tcp", 355);
        
        builder.add("datex-asn", "udp", 355);
        
        builder.add("cloanto-net-1", "tcp", 356);
        
        builder.add("cloanto-net-1", "udp", 356);
        
        builder.add("bhevent", "tcp", 357);
        
        builder.add("bhevent", "udp", 357);
        
        builder.add("shrinkwrap", "tcp", 358);
        
        builder.add("shrinkwrap", "udp", 358);
        
        builder.add("nsrmp", "tcp", 359);
        
        builder.add("nsrmp", "udp", 359);
        
        builder.add("scoi2odialog", "tcp", 360);
        
        builder.add("scoi2odialog", "udp", 360);
        
        builder.add("semantix", "tcp", 361);
        
        builder.add("semantix", "udp", 361);
        
        builder.add("srssend", "tcp", 362);
        
        builder.add("srssend", "udp", 362);
        
        builder.add("rsvp_tunnel", "tcp", 363);
        
        builder.add("rsvp_tunnel", "udp", 363);
        
        builder.add("aurora-cmgr", "tcp", 364);
        
        builder.add("aurora-cmgr", "udp", 364);
        
        builder.add("dtk", "tcp", 365);
        
        builder.add("dtk", "udp", 365);
        
        builder.add("odmr", "tcp", 366);
        
        builder.add("odmr", "udp", 366);
        
        builder.add("mortgageware", "tcp", 367);
        
        builder.add("mortgageware", "udp", 367);
        
        builder.add("qbikgdp", "tcp", 368);
        
        builder.add("qbikgdp", "udp", 368);
        
        builder.add("rpc2portmap", "tcp", 369);
        
        builder.add("rpc2portmap", "udp", 369);
        
        builder.add("codaauth2", "tcp", 370);
        
        builder.add("codaauth2", "udp", 370);
        
        builder.add("clearcase", "tcp", 371);
        
        builder.add("clearcase", "udp", 371);
        
        builder.add("ulistproc", "tcp", 372);
        
        builder.add("ulistproc", "udp", 372);
        
        builder.add("legent-1", "tcp", 373);
        
        builder.add("legent-1", "udp", 373);
        
        builder.add("legent-2", "tcp", 374);
        
        builder.add("legent-2", "udp", 374);
        
        builder.add("hassle", "tcp", 375);
        
        builder.add("hassle", "udp", 375);
        
        builder.add("nip", "tcp", 376);
        
        builder.add("nip", "udp", 376);
        
        builder.add("tnETOS", "tcp", 377);
        
        builder.add("tnETOS", "udp", 377);
        
        builder.add("dsETOS", "tcp", 378);
        
        builder.add("dsETOS", "udp", 378);
        
        builder.add("is99c", "tcp", 379);
        
        builder.add("is99c", "udp", 379);
        
        builder.add("is99s", "tcp", 380);
        
        builder.add("is99s", "udp", 380);
        
        builder.add("hp-collector", "tcp", 381);
        
        builder.add("hp-collector", "udp", 381);
        
        builder.add("hp-managed-node", "tcp", 382);
        
        builder.add("hp-managed-node", "udp", 382);
        
        builder.add("hp-alarm-mgr", "tcp", 383);
        
        builder.add("hp-alarm-mgr", "udp", 383);
        
        builder.add("arns", "tcp", 384);
        
        builder.add("arns", "udp", 384);
        
        builder.add("ibm-app", "tcp", 385);
        
        builder.add("ibm-app", "udp", 385);
        
        builder.add("asa", "tcp", 386);
        
        builder.add("asa", "udp", 386);
        
        builder.add("aurp", "tcp", 387);
        
        builder.add("aurp", "udp", 387);
        
        builder.add("unidata-ldm", "tcp", 388);
        
        builder.add("unidata-ldm", "udp", 388);
        
        builder.add("ldap", "tcp", 389);
        
        builder.add("ldap", "udp", 389);
        
        builder.add("uis", "tcp", 390);
        
        builder.add("uis", "udp", 390);
        
        builder.add("synotics-relay", "tcp", 391);
        
        builder.add("synotics-relay", "udp", 391);
        
        builder.add("synotics-broker", "tcp", 392);
        
        builder.add("synotics-broker", "udp", 392);
        
        builder.add("meta5", "tcp", 393);
        
        builder.add("meta5", "udp", 393);
        
        builder.add("embl-ndt", "tcp", 394);
        
        builder.add("embl-ndt", "udp", 394);
        
        builder.add("netcp", "tcp", 395);
        
        builder.add("netcp", "udp", 395);
        
        builder.add("netware-ip", "tcp", 396);
        
        builder.add("netware-ip", "udp", 396);
        
        builder.add("mptn", "tcp", 397);
        
        builder.add("mptn", "udp", 397);
        
        builder.add("kryptolan", "tcp", 398);
        
        builder.add("kryptolan", "udp", 398);
        
        builder.add("iso-tsap-c2", "tcp", 399);
        
        builder.add("iso-tsap-c2", "udp", 399);
        
        builder.add("work-sol", "tcp", 400);
        
        builder.add("work-sol", "udp", 400);
        
        builder.add("ups", "tcp", 401);
        
        builder.add("ups", "udp", 401);
        
        builder.add("genie", "tcp", 402);
        
        builder.add("genie", "udp", 402);
        
        builder.add("decap", "tcp", 403);
        
        builder.add("decap", "udp", 403);
        
        builder.add("nced", "tcp", 404);
        
        builder.add("nced", "udp", 404);
        
        builder.add("ncld", "tcp", 405);
        
        builder.add("ncld", "udp", 405);
        
        builder.add("imsp", "tcp", 406);
        
        builder.add("imsp", "udp", 406);
        
        builder.add("timbuktu", "tcp", 407);
        
        builder.add("timbuktu", "udp", 407);
        
        builder.add("prm-sm", "tcp", 408);
        
        builder.add("prm-sm", "udp", 408);
        
        builder.add("prm-nm", "tcp", 409);
        
        builder.add("prm-nm", "udp", 409);
        
        builder.add("decladebug", "tcp", 410);
        
        builder.add("decladebug", "udp", 410);
        
        builder.add("rmt", "tcp", 411);
        
        builder.add("rmt", "udp", 411);
        
        builder.add("synoptics-trap", "tcp", 412);
        
        builder.add("synoptics-trap", "udp", 412);
        
        builder.add("smsp", "tcp", 413);
        
        builder.add("smsp", "udp", 413);
        
        builder.add("infoseek", "tcp", 414);
        
        builder.add("infoseek", "udp", 414);
        
        builder.add("bnet", "tcp", 415);
        
        builder.add("bnet", "udp", 415);
        
        builder.add("silverplatter", "tcp", 416);
        
        builder.add("silverplatter", "udp", 416);
        
        builder.add("onmux", "tcp", 417);
        
        builder.add("onmux", "udp", 417);
        
        builder.add("hyper-g", "tcp", 418);
        
        builder.add("hyper-g", "udp", 418);
        
        builder.add("ariel1", "tcp", 419);
        
        builder.add("ariel1", "udp", 419);
        
        builder.add("smpte", "tcp", 420);
        
        builder.add("smpte", "udp", 420);
        
        builder.add("ariel2", "tcp", 421);
        
        builder.add("ariel2", "udp", 421);
        
        builder.add("ariel3", "tcp", 422);
        
        builder.add("ariel3", "udp", 422);
        
        builder.add("opc-job-start", "tcp", 423);
        
        builder.add("opc-job-start", "udp", 423);
        
        builder.add("opc-job-track", "tcp", 424);
        
        builder.add("opc-job-track", "udp", 424);
        
        builder.add("icad-el", "tcp", 425);
        
        builder.add("icad-el", "udp", 425);
        
        builder.add("smartsdp", "tcp", 426);
        
        builder.add("smartsdp", "udp", 426);
        
        builder.add("svrloc", "tcp", 427);
        
        builder.add("svrloc", "udp", 427);
        
        builder.add("ocs_cmu", "tcp", 428);
        
        builder.add("ocs_cmu", "udp", 428);
        
        builder.add("ocs_amu", "tcp", 429);
        
        builder.add("ocs_amu", "udp", 429);
        
        builder.add("utmpsd", "tcp", 430);
        
        builder.add("utmpsd", "udp", 430);
        
        builder.add("utmpcd", "tcp", 431);
        
        builder.add("utmpcd", "udp", 431);
        
        builder.add("iasd", "tcp", 432);
        
        builder.add("iasd", "udp", 432);
        
        builder.add("nnsp", "tcp", 433);
        
        builder.add("nnsp", "udp", 433);
        
        builder.add("mobileip-agent", "tcp", 434);
        
        builder.add("mobileip-agent", "udp", 434);
        
        builder.add("mobilip-mn", "tcp", 435);
        
        builder.add("mobilip-mn", "udp", 435);
        
        builder.add("dna-cml", "tcp", 436);
        
        builder.add("dna-cml", "udp", 436);
        
        builder.add("comscm", "tcp", 437);
        
        builder.add("comscm", "udp", 437);
        
        builder.add("dsfgw", "tcp", 438);
        
        builder.add("dsfgw", "udp", 438);
        
        builder.add("dasp", "tcp", 439);
        
        builder.add("dasp", "udp", 439);
        
        builder.add("sgcp", "tcp", 440);
        
        builder.add("sgcp", "udp", 440);
        
        builder.add("decvms-sysmgt", "tcp", 441);
        
        builder.add("decvms-sysmgt", "udp", 441);
        
        builder.add("cvc_hostd", "tcp", 442);
        
        builder.add("cvc_hostd", "udp", 442);
        
        builder.add("https", "tcp", 443);
        
        builder.add("https", "udp", 443);
        
        builder.add("https/sctp", 443);
        
        builder.add("snpp", "tcp", 444);
        
        builder.add("snpp", "udp", 444);
        
        builder.add("microsoft-ds", "tcp", 445);
        
        builder.add("microsoft-ds", "udp", 445);
        
        builder.add("ddm-rdb", "tcp", 446);
        
        builder.add("ddm-rdb", "udp", 446);
        
        builder.add("ddm-dfm", "tcp", 447);
        
        builder.add("ddm-dfm", "udp", 447);
        
        builder.add("ddm-ssl", "tcp", 448);
        
        builder.add("ddm-ssl", "udp", 448);
        
        builder.add("as-servermap", "tcp", 449);
        
        builder.add("as-servermap", "udp", 449);
        
        builder.add("tserver", "tcp", 450);
        
        builder.add("tserver", "udp", 450);
        
        builder.add("sfs-smp-net", "tcp", 451);
        
        builder.add("sfs-smp-net", "udp", 451);
        
        builder.add("sfs-config", "tcp", 452);
        
        builder.add("sfs-config", "udp", 452);
        
        builder.add("creativeserver", "tcp", 453);
        
        builder.add("creativeserver", "udp", 453);
        
        builder.add("contentserver", "tcp", 454);
        
        builder.add("contentserver", "udp", 454);
        
        builder.add("creativepartnr", "tcp", 455);
        
        builder.add("creativepartnr", "udp", 455);
        
        builder.add("macon-tcp", "tcp", 456);
        
        builder.add("macon-udp", "udp", 456);
        
        builder.add("scohelp", "tcp", 457);
        
        builder.add("scohelp", "udp", 457);
        
        builder.add("appleqtc", "tcp", 458);
        
        builder.add("appleqtc", "udp", 458);
        
        builder.add("ampr-rcmd", "tcp", 459);
        
        builder.add("ampr-rcmd", "udp", 459);
        
        builder.add("skronk", "tcp", 460);
        
        builder.add("skronk", "udp", 460);
        
        builder.add("datasurfsrv", "tcp", 461);
        
        builder.add("datasurfsrv", "udp", 461);
        
        builder.add("datasurfsrvsec", "tcp", 462);
        
        builder.add("datasurfsrvsec", "udp", 462);
        
        builder.add("alpes", "tcp", 463);
        
        builder.add("alpes", "udp", 463);
        
        builder.add("kpasswd", "tcp", 464);
        
        builder.add("kpasswd", "udp", 464);
        
        builder.add("urd", "tcp", 465);
        
        builder.add("igmpv3lite", "udp", 465);
        
        builder.add("digital-vrc", "tcp", 466);
        
        builder.add("digital-vrc", "udp", 466);
        
        builder.add("mylex-mapd", "tcp", 467);
        
        builder.add("mylex-mapd", "udp", 467);
        
        builder.add("photuris", "tcp", 468);
        
        builder.add("photuris", "udp", 468);
        
        builder.add("rcp", "tcp", 469);
        
        builder.add("rcp", "udp", 469);
        
        builder.add("scx-proxy", "tcp", 470);
        
        builder.add("scx-proxy", "udp", 470);
        
        builder.add("mondex", "tcp", 471);
        
        builder.add("mondex", "udp", 471);
        
        builder.add("ljk-login", "tcp", 472);
        
        builder.add("ljk-login", "udp", 472);
        
        builder.add("hybrid-pop", "tcp", 473);
        
        builder.add("hybrid-pop", "udp", 473);
        
        builder.add("tn-tl-w1", "tcp", 474);
        
        builder.add("tn-tl-w2", "udp", 474);
        
        builder.add("tcpnethaspsrv", "tcp", 475);
        
        builder.add("tcpnethaspsrv", "udp", 475);
        
        builder.add("tn-tl-fd1", "tcp", 476);
        
        builder.add("tn-tl-fd1", "udp", 476);
        
        builder.add("ss7ns", "tcp", 477);
        
        builder.add("ss7ns", "udp", 477);
        
        builder.add("spsc", "tcp", 478);
        
        builder.add("spsc", "udp", 478);
        
        builder.add("iafserver", "tcp", 479);
        
        builder.add("iafserver", "udp", 479);
        
        builder.add("iafdbase", "tcp", 480);
        
        builder.add("iafdbase", "udp", 480);
        
        builder.add("ph", "tcp", 481);
        
        builder.add("ph", "udp", 481);
        
        builder.add("bgs-nsi", "tcp", 482);
        
        builder.add("bgs-nsi", "udp", 482);
        
        builder.add("ulpnet", "tcp", 483);
        
        builder.add("ulpnet", "udp", 483);
        
        builder.add("integra-sme", "tcp", 484);
        
        builder.add("integra-sme", "udp", 484);
        
        builder.add("powerburst", "tcp", 485);
        
        builder.add("powerburst", "udp", 485);
        
        builder.add("avian", "tcp", 486);
        
        builder.add("avian", "udp", 486);
        
        builder.add("saft", "tcp", 487);
        
        builder.add("saft", "udp", 487);
        
        builder.add("gss-http", "tcp", 488);
        
        builder.add("gss-http", "udp", 488);
        
        builder.add("nest-protocol", "tcp", 489);
        
        builder.add("nest-protocol", "udp", 489);
        
        builder.add("micom-pfs", "tcp", 490);
        
        builder.add("micom-pfs", "udp", 490);
        
        builder.add("go-login", "tcp", 491);
        
        builder.add("go-login", "udp", 491);
        
        builder.add("ticf-1", "tcp", 492);
        
        builder.add("ticf-1", "udp", 492);
        
        builder.add("ticf-2", "tcp", 493);
        
        builder.add("ticf-2", "udp", 493);
        
        builder.add("pov-ray", "tcp", 494);
        
        builder.add("pov-ray", "udp", 494);
        
        builder.add("intecourier", "tcp", 495);
        
        builder.add("intecourier", "udp", 495);
        
        builder.add("pim-rp-disc", "tcp", 496);
        
        builder.add("pim-rp-disc", "udp", 496);
        
        builder.add("dantz", "tcp", 497);
        
        builder.add("dantz", "udp", 497);
        
        builder.add("siam", "tcp", 498);
        
        builder.add("siam", "udp", 498);
        
        builder.add("iso-ill", "tcp", 499);
        
        builder.add("iso-ill", "udp", 499);
        
        builder.add("isakmp", "tcp", 500);
        
        builder.add("isakmp", "udp", 500);
        
        builder.add("stmf", "tcp", 501);
        
        builder.add("stmf", "udp", 501);
        
        builder.add("asa-appl-proto", "tcp", 502);
        
        builder.add("asa-appl-proto", "udp", 502);
        
        builder.add("intrinsa", "tcp", 503);
        
        builder.add("intrinsa", "udp", 503);
        
        builder.add("citadel", "tcp", 504);
        
        builder.add("citadel", "udp", 504);
        
        builder.add("mailbox-lm", "tcp", 505);
        
        builder.add("mailbox-lm", "udp", 505);
        
        builder.add("ohimsrv", "tcp", 506);
        
        builder.add("ohimsrv", "udp", 506);
        
        builder.add("crs", "tcp", 507);
        
        builder.add("crs", "udp", 507);
        
        builder.add("xvttp", "tcp", 508);
        
        builder.add("xvttp", "udp", 508);
        
        builder.add("snare", "tcp", 509);
        
        builder.add("snare", "udp", 509);
        
        builder.add("fcp", "tcp", 510);
        
        builder.add("fcp", "udp", 510);
        
        builder.add("passgo", "tcp", 511);
        
        builder.add("passgo", "udp", 511);
        
        builder.add("exec", "tcp", 512);
        
        builder.add("comsat", "udp", 512);
        
        builder.add("biff", "udp", 512);
        
        builder.add("login", "tcp", 513);
        
        builder.add("who", "udp", 513);
        
        builder.add("shell", "tcp", 514);
        
        builder.add("syslog", "udp", 514);
        
        builder.add("printer", "tcp", 515);
        
        builder.add("printer", "udp", 515);
        
        builder.add("videotex", "tcp", 516);
        
        builder.add("videotex", "udp", 516);
        
        builder.add("talk", "tcp", 517);
        
        builder.add("talk", "udp", 517);
        
        builder.add("ntalk", "tcp", 518);
        
        builder.add("ntalk", "udp", 518);
        
        builder.add("utime", "tcp", 519);
        
        builder.add("utime", "udp", 519);
        
        builder.add("efs", "tcp", 520);
        
        builder.add("router", "udp", 520);
        
        builder.add("ripng", "tcp", 521);
        
        builder.add("ripng", "udp", 521);
        
        builder.add("ulp", "tcp", 522);
        
        builder.add("ulp", "udp", 522);
        
        builder.add("ibm-db2", "tcp", 523);
        
        builder.add("ibm-db2", "udp", 523);
        
        builder.add("ncp", "tcp", 524);
        
        builder.add("ncp", "udp", 524);
        
        builder.add("timed", "tcp", 525);
        
        builder.add("timed", "udp", 525);
        
        builder.add("tempo", "tcp", 526);
        
        builder.add("tempo", "udp", 526);
        
        builder.add("stx", "tcp", 527);
        
        builder.add("stx", "udp", 527);
        
        builder.add("custix", "tcp", 528);
        
        builder.add("custix", "udp", 528);
        
        builder.add("irc-serv", "tcp", 529);
        
        builder.add("irc-serv", "udp", 529);
        
        builder.add("courier", "tcp", 530);
        
        builder.add("courier", "udp", 530);
        
        builder.add("conference", "tcp", 531);
        
        builder.add("conference", "udp", 531);
        
        builder.add("netnews", "tcp", 532);
        
        builder.add("netnews", "udp", 532);
        
        builder.add("netwall", "tcp", 533);
        
        builder.add("netwall", "udp", 533);
        
        builder.add("windream", "tcp", 534);
        
        builder.add("windream", "udp", 534);
        
        builder.add("iiop", "tcp", 535);
        
        builder.add("iiop", "udp", 535);
        
        builder.add("opalis-rdv", "tcp", 536);
        
        builder.add("opalis-rdv", "udp", 536);
        
        builder.add("nmsp", "tcp", 537);
        
        builder.add("nmsp", "udp", 537);
        
        builder.add("gdomap", "tcp", 538);
        
        builder.add("gdomap", "udp", 538);
        
        builder.add("apertus-ldp", "tcp", 539);
        
        builder.add("apertus-ldp", "udp", 539);
        
        builder.add("uucp", "tcp", 540);
        
        builder.add("uucp", "udp", 540);
        
        builder.add("uucp-rlogin", "tcp", 541);
        
        builder.add("uucp-rlogin", "udp", 541);
        
        builder.add("commerce", "tcp", 542);
        
        builder.add("commerce", "udp", 542);
        
        builder.add("klogin", "tcp", 543);
        
        builder.add("klogin", "udp", 543);
        
        builder.add("kshell", "tcp", 544);
        
        builder.add("kshell", "udp", 544);
        
        builder.add("appleqtcsrvr", "tcp", 545);
        
        builder.add("appleqtcsrvr", "udp", 545);
        
        builder.add("dhcpv6-client", "tcp", 546);
        
        builder.add("dhcpv6-client", "udp", 546);
        
        builder.add("dhcpv6-server", "tcp", 547);
        
        builder.add("dhcpv6-server", "udp", 547);
        
        builder.add("afpovertcp", "tcp", 548);
        
        builder.add("afpovertcp", "udp", 548);
        
        builder.add("idfp", "tcp", 549);
        
        builder.add("idfp", "udp", 549);
        
        builder.add("new-rwho", "tcp", 550);
        
        builder.add("new-rwho", "udp", 550);
        
        builder.add("cybercash", "tcp", 551);
        
        builder.add("cybercash", "udp", 551);
        
        builder.add("devshr-nts", "tcp", 552);
        
        builder.add("devshr-nts", "udp", 552);
        
        builder.add("pirp", "tcp", 553);
        
        builder.add("pirp", "udp", 553);
        
        builder.add("rtsp", "tcp", 554);
        
        builder.add("rtsp", "udp", 554);
        
        builder.add("dsf", "tcp", 555);
        
        builder.add("dsf", "udp", 555);
        
        builder.add("remotefs", "tcp", 556);
        
        builder.add("remotefs", "udp", 556);
        
        builder.add("openvms-sysipc", "tcp", 557);
        
        builder.add("openvms-sysipc", "udp", 557);
        
        builder.add("sdnskmp", "tcp", 558);
        
        builder.add("sdnskmp", "udp", 558);
        
        builder.add("teedtap", "tcp", 559);
        
        builder.add("teedtap", "udp", 559);
        
        builder.add("rmonitor", "tcp", 560);
        
        builder.add("rmonitor", "udp", 560);
        
        builder.add("monitor", "tcp", 561);
        
        builder.add("monitor", "udp", 561);
        
        builder.add("chshell", "tcp", 562);
        
        builder.add("chshell", "udp", 562);
        
        builder.add("nntps", "tcp", 563);
        
        builder.add("nntps", "udp", 563);
        
        builder.add("9pfs", "tcp", 564);
        
        builder.add("9pfs", "udp", 564);
        
        builder.add("whoami", "tcp", 565);
        
        builder.add("whoami", "udp", 565);
        
        builder.add("streettalk", "tcp", 566);
        
        builder.add("streettalk", "udp", 566);
        
        builder.add("banyan-rpc", "tcp", 567);
        
        builder.add("banyan-rpc", "udp", 567);
        
        builder.add("ms-shuttle", "tcp", 568);
        
        builder.add("ms-shuttle", "udp", 568);
        
        builder.add("ms-rome", "tcp", 569);
        
        builder.add("ms-rome", "udp", 569);
        
        builder.add("meter", "tcp", 570);
        
        builder.add("meter", "udp", 570);
        
        builder.add("meter", "tcp", 571);
        
        builder.add("meter", "udp", 571);
        
        builder.add("sonar", "tcp", 572);
        
        builder.add("sonar", "udp", 572);
        
        builder.add("banyan-vip", "tcp", 573);
        
        builder.add("banyan-vip", "udp", 573);
        
        builder.add("ftp-agent", "tcp", 574);
        
        builder.add("ftp-agent", "udp", 574);
        
        builder.add("vemmi", "tcp", 575);
        
        builder.add("vemmi", "udp", 575);
        
        builder.add("ipcd", "tcp", 576);
        
        builder.add("ipcd", "udp", 576);
        
        builder.add("vnas", "tcp", 577);
        
        builder.add("vnas", "udp", 577);
        
        builder.add("ipdd", "tcp", 578);
        
        builder.add("ipdd", "udp", 578);
        
        builder.add("decbsrv", "tcp", 579);
        
        builder.add("decbsrv", "udp", 579);
        
        builder.add("sntp-heartbeat", "tcp", 580);
        
        builder.add("sntp-heartbeat", "udp", 580);
        
        builder.add("bdp", "tcp", 581);
        
        builder.add("bdp", "udp", 581);
        
        builder.add("scc-security", "tcp", 582);
        
        builder.add("scc-security", "udp", 582);
        
        builder.add("philips-vc", "tcp", 583);
        
        builder.add("philips-vc", "udp", 583);
        
        builder.add("keyserver", "tcp", 584);
        
        builder.add("keyserver", "udp", 584);
        
        builder.add("password-chg", "tcp", 586);
        
        builder.add("password-chg", "udp", 586);
        
        builder.add("submission", "tcp", 587);
        
        builder.add("submission", "udp", 587);
        
        builder.add("cal", "tcp", 588);
        
        builder.add("cal", "udp", 588);
        
        builder.add("eyelink", "tcp", 589);
        
        builder.add("eyelink", "udp", 589);
        
        builder.add("tns-cml", "tcp", 590);
        
        builder.add("tns-cml", "udp", 590);
        
        builder.add("http-alt", "tcp", 591);
        
        builder.add("http-alt", "udp", 591);
        
        builder.add("eudora-set", "tcp", 592);
        
        builder.add("eudora-set", "udp", 592);
        
        builder.add("http-rpc-epmap", "tcp", 593);
        
        builder.add("http-rpc-epmap", "udp", 593);
        
        builder.add("tpip", "tcp", 594);
        
        builder.add("tpip", "udp", 594);
        
        builder.add("cab-protocol", "tcp", 595);
        
        builder.add("cab-protocol", "udp", 595);
        
        builder.add("smsd", "tcp", 596);
        
        builder.add("smsd", "udp", 596);
        
        builder.add("ptcnameservice", "tcp", 597);
        
        builder.add("ptcnameservice", "udp", 597);
        
        builder.add("sco-websrvrmg3", "tcp", 598);
        
        builder.add("sco-websrvrmg3", "udp", 598);
        
        builder.add("acp", "tcp", 599);
        
        builder.add("acp", "udp", 599);
        
        builder.add("ipcserver", "tcp", 600);
        
        builder.add("ipcserver", "udp", 600);
        
        builder.add("syslog-conn", "tcp", 601);
        
        builder.add("syslog-conn", "udp", 601);
        
        builder.add("xmlrpc-beep", "tcp", 602);
        
        builder.add("xmlrpc-beep", "udp", 602);
        
        builder.add("idxp", "tcp", 603);
        
        builder.add("idxp", "udp", 603);
        
        builder.add("tunnel", "tcp", 604);
        
        builder.add("tunnel", "udp", 604);
        
        builder.add("soap-beep", "tcp", 605);
        
        builder.add("soap-beep", "udp", 605);
        
        builder.add("urm", "tcp", 606);
        
        builder.add("urm", "udp", 606);
        
        builder.add("nqs", "tcp", 607);
        
        builder.add("nqs", "udp", 607);
        
        builder.add("sift-uft", "tcp", 608);
        
        builder.add("sift-uft", "udp", 608);
        
        builder.add("npmp-trap", "tcp", 609);
        
        builder.add("npmp-trap", "udp", 609);
        
        builder.add("npmp-local", "tcp", 610);
        
        builder.add("npmp-local", "udp", 610);
        
        builder.add("npmp-gui", "tcp", 611);
        
        builder.add("npmp-gui", "udp", 611);
        
        builder.add("hmmp-ind", "tcp", 612);
        
        builder.add("hmmp-ind", "udp", 612);
        
        builder.add("hmmp-op", "tcp", 613);
        
        builder.add("hmmp-op", "udp", 613);
        
        builder.add("sshell", "tcp", 614);
        
        builder.add("sshell", "udp", 614);
        
        builder.add("sco-inetmgr", "tcp", 615);
        
        builder.add("sco-inetmgr", "udp", 615);
        
        builder.add("sco-sysmgr", "tcp", 616);
        
        builder.add("sco-sysmgr", "udp", 616);
        
        builder.add("sco-dtmgr", "tcp", 617);
        
        builder.add("sco-dtmgr", "udp", 617);
        
        builder.add("dei-icda", "tcp", 618);
        
        builder.add("dei-icda", "udp", 618);
        
        builder.add("compaq-evm", "tcp", 619);
        
        builder.add("compaq-evm", "udp", 619);
        
        builder.add("sco-websrvrmgr", "tcp", 620);
        
        builder.add("sco-websrvrmgr", "udp", 620);
        
        builder.add("escp-ip", "tcp", 621);
        
        builder.add("escp-ip", "udp", 621);
        
        builder.add("collaborator", "tcp", 622);
        
        builder.add("collaborator", "udp", 622);
        
        builder.add("oob-ws-http", "tcp", 623);
        
        builder.add("asf-rmcp", "udp", 623);
        
        builder.add("cryptoadmin", "tcp", 624);
        
        builder.add("cryptoadmin", "udp", 624);
        
        builder.add("dec_dlm", "tcp", 625);
        
        builder.add("dec_dlm", "udp", 625);
        
        builder.add("asia", "tcp", 626);
        
        builder.add("asia", "udp", 626);
        
        builder.add("passgo-tivoli", "tcp", 627);
        
        builder.add("passgo-tivoli", "udp", 627);
        
        builder.add("qmqp", "tcp", 628);
        
        builder.add("qmqp", "udp", 628);
        
        builder.add("3com-amp3", "tcp", 629);
        
        builder.add("3com-amp3", "udp", 629);
        
        builder.add("rda", "tcp", 630);
        
        builder.add("rda", "udp", 630);
        
        builder.add("ipp", "tcp", 631);
        
        builder.add("ipp", "udp", 631);
        
        builder.add("bmpp", "tcp", 632);
        
        builder.add("bmpp", "udp", 632);
        
        builder.add("servstat", "tcp", 633);
        
        builder.add("servstat", "udp", 633);
        
        builder.add("ginad", "tcp", 634);
        
        builder.add("ginad", "udp", 634);
        
        builder.add("rlzdbase", "tcp", 635);
        
        builder.add("rlzdbase", "udp", 635);
        
        builder.add("ldaps", "tcp", 636);
        
        builder.add("ldaps", "udp", 636);
        
        builder.add("lanserver", "tcp", 637);
        
        builder.add("lanserver", "udp", 637);
        
        builder.add("mcns-sec", "tcp", 638);
        
        builder.add("mcns-sec", "udp", 638);
        
        builder.add("msdp", "tcp", 639);
        
        builder.add("msdp", "udp", 639);
        
        builder.add("entrust-sps", "tcp", 640);
        
        builder.add("entrust-sps", "udp", 640);
        
        builder.add("repcmd", "tcp", 641);
        
        builder.add("repcmd", "udp", 641);
        
        builder.add("esro-emsdp", "tcp", 642);
        
        builder.add("esro-emsdp", "udp", 642);
        
        builder.add("sanity", "tcp", 643);
        
        builder.add("sanity", "udp", 643);
        
        builder.add("dwr", "tcp", 644);
        
        builder.add("dwr", "udp", 644);
        
        builder.add("pssc", "tcp", 645);
        
        builder.add("pssc", "udp", 645);
        
        builder.add("ldp", "tcp", 646);
        
        builder.add("ldp", "udp", 646);
        
        builder.add("dhcp-failover", "tcp", 647);
        
        builder.add("dhcp-failover", "udp", 647);
        
        builder.add("rrp", "tcp", 648);
        
        builder.add("rrp", "udp", 648);
        
        builder.add("cadview-3d", "tcp", 649);
        
        builder.add("cadview-3d", "udp", 649);
        
        builder.add("obex", "tcp", 650);
        
        builder.add("obex", "udp", 650);
        
        builder.add("ieee-mms", "tcp", 651);
        
        builder.add("ieee-mms", "udp", 651);
        
        builder.add("hello-port", "tcp", 652);
        
        builder.add("hello-port", "udp", 652);
        
        builder.add("repscmd", "tcp", 653);
        
        builder.add("repscmd", "udp", 653);
        
        builder.add("aodv", "tcp", 654);
        
        builder.add("aodv", "udp", 654);
        
        builder.add("tinc", "tcp", 655);
        
        builder.add("tinc", "udp", 655);
        
        builder.add("spmp", "tcp", 656);
        
        builder.add("spmp", "udp", 656);
        
        builder.add("rmc", "tcp", 657);
        
        builder.add("rmc", "udp", 657);
        
        builder.add("tenfold", "tcp", 658);
        
        builder.add("tenfold", "udp", 658);
        
        builder.add("mac-srvr-admin", "tcp", 660);
        
        builder.add("mac-srvr-admin", "udp", 660);
        
        builder.add("hap", "tcp", 661);
        
        builder.add("hap", "udp", 661);
        
        builder.add("pftp", "tcp", 662);
        
        builder.add("pftp", "udp", 662);
        
        builder.add("purenoise", "tcp", 663);
        
        builder.add("purenoise", "udp", 663);
        
        builder.add("oob-ws-https", "tcp", 664);
        
        builder.add("asf-secure-rmcp", "udp", 664);
        
        builder.add("sun-dr", "tcp", 665);
        
        builder.add("sun-dr", "udp", 665);
        
        builder.add("mdqs", "tcp", 666);
        
        builder.add("mdqs", "udp", 666);
        
        builder.add("doom", "tcp", 666);
        
        builder.add("doom", "udp", 666);
        
        builder.add("disclose", "tcp", 667);
        
        builder.add("disclose", "udp", 667);
        
        builder.add("mecomm", "tcp", 668);
        
        builder.add("mecomm", "udp", 668);
        
        builder.add("meregister", "tcp", 669);
        
        builder.add("meregister", "udp", 669);
        
        builder.add("vacdsm-sws", "tcp", 670);
        
        builder.add("vacdsm-sws", "udp", 670);
        
        builder.add("vacdsm-app", "tcp", 671);
        
        builder.add("vacdsm-app", "udp", 671);
        
        builder.add("vpps-qua", "tcp", 672);
        
        builder.add("vpps-qua", "udp", 672);
        
        builder.add("cimplex", "tcp", 673);
        
        builder.add("cimplex", "udp", 673);
        
        builder.add("acap", "tcp", 674);
        
        builder.add("acap", "udp", 674);
        
        builder.add("dctp", "tcp", 675);
        
        builder.add("dctp", "udp", 675);
        
        builder.add("vpps-via", "tcp", 676);
        
        builder.add("vpps-via", "udp", 676);
        
        builder.add("vpp", "tcp", 677);
        
        builder.add("vpp", "udp", 677);
        
        builder.add("ggf-ncp", "tcp", 678);
        
        builder.add("ggf-ncp", "udp", 678);
        
        builder.add("mrm", "tcp", 679);
        
        builder.add("mrm", "udp", 679);
        
        builder.add("entrust-aaas", "tcp", 680);
        
        builder.add("entrust-aaas", "udp", 680);
        
        builder.add("entrust-aams", "tcp", 681);
        
        builder.add("entrust-aams", "udp", 681);
        
        builder.add("xfr", "tcp", 682);
        
        builder.add("xfr", "udp", 682);
        
        builder.add("corba-iiop", "tcp", 683);
        
        builder.add("corba-iiop", "udp", 683);
        
        builder.add("corba-iiop-ssl", "tcp", 684);
        
        builder.add("corba-iiop-ssl", "udp", 684);
        
        builder.add("mdc-portmapper", "tcp", 685);
        
        builder.add("mdc-portmapper", "udp", 685);
        
        builder.add("hcp-wismar", "tcp", 686);
        
        builder.add("hcp-wismar", "udp", 686);
        
        builder.add("asipregistry", "tcp", 687);
        
        builder.add("asipregistry", "udp", 687);
        
        builder.add("realm-rusd", "tcp", 688);
        
        builder.add("realm-rusd", "udp", 688);
        
        builder.add("nmap", "tcp", 689);
        
        builder.add("nmap", "udp", 689);
        
        builder.add("vatp", "tcp", 690);
        
        builder.add("vatp", "udp", 690);
        
        builder.add("msexch-routing", "tcp", 691);
        
        builder.add("msexch-routing", "udp", 691);
        
        builder.add("hyperwave-isp", "tcp", 692);
        
        builder.add("hyperwave-isp", "udp", 692);
        
        builder.add("connendp", "tcp", 693);
        
        builder.add("connendp", "udp", 693);
        
        builder.add("ha-cluster", "tcp", 694);
        
        builder.add("ha-cluster", "udp", 694);
        
        builder.add("ieee-mms-ssl", "tcp", 695);
        
        builder.add("ieee-mms-ssl", "udp", 695);
        
        builder.add("rushd", "tcp", 696);
        
        builder.add("rushd", "udp", 696);
        
        builder.add("uuidgen", "tcp", 697);
        
        builder.add("uuidgen", "udp", 697);
        
        builder.add("olsr", "tcp", 698);
        
        builder.add("olsr", "udp", 698);
        
        builder.add("accessnetwork", "tcp", 699);
        
        builder.add("accessnetwork", "udp", 699);
        
        builder.add("epp", "tcp", 700);
        
        builder.add("epp", "udp", 700);
        
        builder.add("lmp", "tcp", 701);
        
        builder.add("lmp", "udp", 701);
        
        builder.add("iris-beep", "tcp", 702);
        
        builder.add("iris-beep", "udp", 702);
        
        builder.add("elcsd", "tcp", 704);
        
        builder.add("elcsd", "udp", 704);
        
        builder.add("agentx", "tcp", 705);
        
        builder.add("agentx", "udp", 705);
        
        builder.add("silc", "tcp", 706);
        
        builder.add("silc", "udp", 706);
        
        builder.add("borland-dsj", "tcp", 707);
        
        builder.add("borland-dsj", "udp", 707);
        
        builder.add("entrust-kmsh", "tcp", 709);
        
        builder.add("entrust-kmsh", "udp", 709);
        
        builder.add("entrust-ash", "tcp", 710);
        
        builder.add("entrust-ash", "udp", 710);
        
        builder.add("cisco-tdp", "tcp", 711);
        
        builder.add("cisco-tdp", "udp", 711);
        
        builder.add("tbrpf", "tcp", 712);
        
        builder.add("tbrpf", "udp", 712);
        
        builder.add("iris-xpc", "tcp", 713);
        
        builder.add("iris-xpc", "udp", 713);
        
        builder.add("iris-xpcs", "tcp", 714);
        
        builder.add("iris-xpcs", "udp", 714);
        
        builder.add("iris-lwz", "tcp", 715);
        
        builder.add("iris-lwz", "udp", 715);
        
        builder.add("pana", "udp", 716);
        
        builder.add("netviewdm1", "tcp", 729);
        
        builder.add("netviewdm1", "udp", 729);
        
        builder.add("netviewdm2", "tcp", 730);
        
        builder.add("netviewdm2", "udp", 730);
        
        builder.add("netviewdm3", "tcp", 731);
        
        builder.add("netviewdm3", "udp", 731);
        
        builder.add("netgw", "tcp", 741);
        
        builder.add("netgw", "udp", 741);
        
        builder.add("netrcs", "tcp", 742);
        
        builder.add("netrcs", "udp", 742);
        
        builder.add("flexlm", "tcp", 744);
        
        builder.add("flexlm", "udp", 744);
        
        builder.add("fujitsu-dev", "tcp", 747);
        
        builder.add("fujitsu-dev", "udp", 747);
        
        builder.add("ris-cm", "tcp", 748);
        
        builder.add("ris-cm", "udp", 748);
        
        builder.add("kerberos-adm", "tcp", 749);
        
        builder.add("kerberos-adm", "udp", 749);
        
        builder.add("rfile", "tcp", 750);
        
        builder.add("loadav", "udp", 750);
        
        builder.add("kerberos-iv", "udp", 750);
        
        builder.add("pump", "tcp", 751);
        
        builder.add("pump", "udp", 751);
        
        builder.add("qrh", "tcp", 752);
        
        builder.add("qrh", "udp", 752);
        
        builder.add("rrh", "tcp", 753);
        
        builder.add("rrh", "udp", 753);
        
        builder.add("tell", "tcp", 754);
        
        builder.add("tell", "udp", 754);
        
        builder.add("nlogin", "tcp", 758);
        
        builder.add("nlogin", "udp", 758);
        
        builder.add("con", "tcp", 759);
        
        builder.add("con", "udp", 759);
        
        builder.add("ns", "tcp", 760);
        
        builder.add("ns", "udp", 760);
        
        builder.add("rxe", "tcp", 761);
        
        builder.add("rxe", "udp", 761);
        
        builder.add("quotad", "tcp", 762);
        
        builder.add("quotad", "udp", 762);
        
        builder.add("cycleserv", "tcp", 763);
        
        builder.add("cycleserv", "udp", 763);
        
        builder.add("omserv", "tcp", 764);
        
        builder.add("omserv", "udp", 764);
        
        builder.add("webster", "tcp", 765);
        
        builder.add("webster", "udp", 765);
        
        builder.add("phonebook", "tcp", 767);
        
        builder.add("phonebook", "udp", 767);
        
        builder.add("vid", "tcp", 769);
        
        builder.add("vid", "udp", 769);
        
        builder.add("cadlock", "tcp", 770);
        
        builder.add("cadlock", "udp", 770);
        
        builder.add("rtip", "tcp", 771);
        
        builder.add("rtip", "udp", 771);
        
        builder.add("cycleserv2", "tcp", 772);
        
        builder.add("cycleserv2", "udp", 772);
        
        builder.add("submit", "tcp", 773);
        
        builder.add("notify", "udp", 773);
        
        builder.add("rpasswd", "tcp", 774);
        
        builder.add("acmaint_dbd", "udp", 774);
        
        builder.add("entomb", "tcp", 775);
        
        builder.add("acmaint_transd", "udp", 775);
        
        builder.add("wpages", "tcp", 776);
        
        builder.add("wpages", "udp", 776);
        
        builder.add("multiling-http", "tcp", 777);
        
        builder.add("multiling-http", "udp", 777);
        
        builder.add("wpgs", "tcp", 780);
        
        builder.add("wpgs", "udp", 780);
        
        builder.add("mdbs_daemon", "tcp", 800);
        
        builder.add("mdbs_daemon", "udp", 800);
        
        builder.add("device", "tcp", 801);
        
        builder.add("device", "udp", 801);
        
        builder.add("fcp-udp", "tcp", 810);
        
        builder.add("fcp-udp", "udp", 810);
        
        builder.add("itm-mcell-s", "tcp", 828);
        
        builder.add("itm-mcell-s", "udp", 828);
        
        builder.add("pkix-3-ca-ra", "tcp", 829);
        
        builder.add("pkix-3-ca-ra", "udp", 829);
        
        builder.add("netconf-ssh", "tcp", 830);
        
        builder.add("netconf-ssh", "udp", 830);
        
        builder.add("netconf-beep", "tcp", 831);
        
        builder.add("netconf-beep", "udp", 831);
        
        builder.add("netconfsoaphttp", "tcp", 832);
        
        builder.add("netconfsoaphttp", "udp", 832);
        
        builder.add("netconfsoapbeep", "tcp", 833);
        
        builder.add("netconfsoapbeep", "udp", 833);
        
        builder.add("dhcp-failover2", "tcp", 847);
        
        builder.add("dhcp-failover2", "udp", 847);
        
        builder.add("gdoi", "tcp", 848);
        
        builder.add("gdoi", "udp", 848);
        
        builder.add("iscsi", "tcp", 860);
        
        builder.add("iscsi", "udp", 860);
        
        builder.add("owamp-control", "tcp", 861);
        
        builder.add("owamp-control", "udp", 861);
        
        builder.add("rsync", "tcp", 873);
        
        builder.add("rsync", "udp", 873);
        
        builder.add("iclcnet-locate", "tcp", 886);
        
        builder.add("iclcnet-locate", "udp", 886);
        
        builder.add("iclcnet_svinfo", "tcp", 887);
        
        builder.add("iclcnet_svinfo", "udp", 887);
        
        builder.add("accessbuilder", "tcp", 888);
        
        builder.add("accessbuilder", "udp", 888);
        
        builder.add("cddbp", "tcp", 888);
        
        builder.add("omginitialrefs", "tcp", 900);
        
        builder.add("omginitialrefs", "udp", 900);
        
        builder.add("smpnameres", "tcp", 901);
        
        builder.add("smpnameres", "udp", 901);
        
        builder.add("ideafarm-door", "tcp", 902);
        
        builder.add("ideafarm-door", "udp", 902);
        
        builder.add("ideafarm-panic", "tcp", 903);
        
        builder.add("ideafarm-panic", "udp", 903);
        
        builder.add("kink", "tcp", 910);
        
        builder.add("kink", "udp", 910);
        
        builder.add("xact-backup", "tcp", 911);
        
        builder.add("xact-backup", "udp", 911);
        
        builder.add("apex-mesh", "tcp", 912);
        
        builder.add("apex-mesh", "udp", 912);
        
        builder.add("apex-edge", "tcp", 913);
        
        builder.add("apex-edge", "udp", 913);
        
        builder.add("ftps-data", "tcp", 989);
        
        builder.add("ftps-data", "udp", 989);
        
        builder.add("ftps", "tcp", 990);
        
        builder.add("ftps", "udp", 990);
        
        builder.add("nas", "tcp", 991);
        
        builder.add("nas", "udp", 991);
        
        builder.add("telnets", "tcp", 992);
        
        builder.add("telnets", "udp", 992);
        
        builder.add("imaps", "tcp", 993);
        
        builder.add("imaps", "udp", 993);
        
        builder.add("ircs", "tcp", 994);
        
        builder.add("ircs", "udp", 994);
        
        builder.add("pop3s", "tcp", 995);
        
        builder.add("pop3s", "udp", 995);
        
        builder.add("vsinet", "tcp", 996);
        
        builder.add("vsinet", "udp", 996);
        
        builder.add("maitrd", "tcp", 997);
        
        builder.add("maitrd", "udp", 997);
        
        builder.add("busboy", "tcp", 998);
        
        builder.add("puparp", "udp", 998);
        
        builder.add("garcon", "tcp", 999);
        
        builder.add("applix", "udp", 999);
        
        builder.add("puprouter", "tcp", 999);
        
        builder.add("puprouter", "udp", 999);
        
        builder.add("cadlock2", "tcp", 1000);
        
        builder.add("cadlock2", "udp", 1000);
        
        builder.add("surf", "tcp", 1010);
        
        builder.add("surf", "udp", 1010);
        
        builder.add("exp1", "tcp", 1021);
        
        builder.add("exp1", "udp", 1021);
        
        builder.add("exp2", "tcp", 1022);
        
        builder.add("exp2", "udp", 1022);
        
        
        return builder.build();
    }
}
