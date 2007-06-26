/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.editors.dataType;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TimeZoneContentProvider implements IStructuredContentProvider {
	private static String[] TIME_ZONES;

	public static final Object BLANK_VALUE = "";

	public Object[] getElements(Object _inputElement) {
		return TimeZoneContentProvider.TIME_ZONES;
	}

	public void dispose() {
		// DO NOTHING
	}

	public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
		// DO NOTHING
	}

	static {
		List<String> timeZoneList = new LinkedList<String>();
		timeZoneList.add("");
		timeZoneList.add("Africa/Abidjan");
		timeZoneList.add("Africa/Accra");
		timeZoneList.add("Africa/Addis_Ababa");
		timeZoneList.add("Africa/Algiers");
		timeZoneList.add("Africa/Asmera");
		timeZoneList.add("Africa/Bamako");
		timeZoneList.add("Africa/Bangui");
		timeZoneList.add("Africa/Banjul");
		timeZoneList.add("Africa/Bissau");
		timeZoneList.add("Africa/Blantyre");
		timeZoneList.add("Africa/Brazzaville");
		timeZoneList.add("Africa/Bujumbura");
		timeZoneList.add("Africa/Cairo");
		timeZoneList.add("Africa/Casablanca");
		timeZoneList.add("Africa/Ceuta");
		timeZoneList.add("Africa/Conakry");
		timeZoneList.add("Africa/Dakar");
		timeZoneList.add("Africa/Dar_es_Salaam");
		timeZoneList.add("Africa/Djibouti");
		timeZoneList.add("Africa/Douala");
		timeZoneList.add("Africa/El_Aaiun");
		timeZoneList.add("Africa/Freetown");
		timeZoneList.add("Africa/Gaborone");
		timeZoneList.add("Africa/Harare");
		timeZoneList.add("Africa/Johannesburg");
		timeZoneList.add("Africa/Kampala");
		timeZoneList.add("Africa/Khartoum");
		timeZoneList.add("Africa/Kigali");
		timeZoneList.add("Africa/Kinshasa");
		timeZoneList.add("Africa/Lagos");
		timeZoneList.add("Africa/Libreville");
		timeZoneList.add("Africa/Lome");
		timeZoneList.add("Africa/Luanda");
		timeZoneList.add("Africa/Lubumbashi");
		timeZoneList.add("Africa/Lusaka");
		timeZoneList.add("Africa/Malabo");
		timeZoneList.add("Africa/Maputo");
		timeZoneList.add("Africa/Maseru");
		timeZoneList.add("Africa/Mbabane");
		timeZoneList.add("Africa/Mogadishu");
		timeZoneList.add("Africa/Monrovia");
		timeZoneList.add("Africa/Nairobi");
		timeZoneList.add("Africa/Ndjamena");
		timeZoneList.add("Africa/Niamey");
		timeZoneList.add("Africa/Nouakchott");
		timeZoneList.add("Africa/Ouagadougou");
		timeZoneList.add("Africa/Porto-Novo");
		timeZoneList.add("Africa/Sao_Tome");
		timeZoneList.add("Africa/Timbuktu");
		timeZoneList.add("Africa/Tripoli");
		timeZoneList.add("Africa/Tunis");
		timeZoneList.add("Africa/Windhoek");
		timeZoneList.add("America/Adak");
		timeZoneList.add("America/Anchorage");
		timeZoneList.add("America/Anguilla");
		timeZoneList.add("America/Antigua");
		timeZoneList.add("America/Araguaina");
		timeZoneList.add("America/Aruba");
		timeZoneList.add("America/Asuncion");
		timeZoneList.add("America/Atka");
		timeZoneList.add("America/Barbados");
		timeZoneList.add("America/Belem");
		timeZoneList.add("America/Belize");
		timeZoneList.add("America/Boa_Vista");
		timeZoneList.add("America/Bogota");
		timeZoneList.add("America/Boise");
		timeZoneList.add("America/Buenos_Aires");
		timeZoneList.add("America/Cambridge_Bay");
		timeZoneList.add("America/Cancun");
		timeZoneList.add("America/Caracas");
		timeZoneList.add("America/Catamarca");
		timeZoneList.add("America/Cayenne");
		timeZoneList.add("America/Cayman");
		timeZoneList.add("America/Chicago");
		timeZoneList.add("America/Chihuahua");
		timeZoneList.add("America/Cordoba");
		timeZoneList.add("America/Costa_Rica");
		timeZoneList.add("America/Cuiaba");
		timeZoneList.add("America/Curacao");
		timeZoneList.add("America/Dawson");
		timeZoneList.add("America/Dawson_Creek");
		timeZoneList.add("America/Denver");
		timeZoneList.add("America/Detroit");
		timeZoneList.add("America/Dominica");
		timeZoneList.add("America/Edmonton");
		timeZoneList.add("America/Eirunepe");
		timeZoneList.add("America/El_Salvador");
		timeZoneList.add("America/Ensenada");
		timeZoneList.add("America/Fort_Wayne");
		timeZoneList.add("America/Fortaleza");
		timeZoneList.add("America/Glace_Bay");
		timeZoneList.add("America/Godthab");
		timeZoneList.add("America/Goose_Bay");
		timeZoneList.add("America/Grand_Turk");
		timeZoneList.add("America/Grenada");
		timeZoneList.add("America/Guadeloupe");
		timeZoneList.add("America/Guatemala");
		timeZoneList.add("America/Guayaquil");
		timeZoneList.add("America/Guyana");
		timeZoneList.add("America/Halifax");
		timeZoneList.add("America/Havana");
		timeZoneList.add("America/Hermosillo");
		timeZoneList.add("America/Indiana/Indianapolis");
		timeZoneList.add("America/Indiana/Knox");
		timeZoneList.add("America/Indiana/Marengo");
		timeZoneList.add("America/Indiana/Vevay");
		timeZoneList.add("America/Indianapolis");
		timeZoneList.add("America/Inuvik");
		timeZoneList.add("America/Iqaluit");
		timeZoneList.add("America/Jamaica");
		timeZoneList.add("America/Jujuy");
		timeZoneList.add("America/Juneau");
		timeZoneList.add("America/Kentucky/Louisville");
		timeZoneList.add("America/Kentucky/Monticello");
		timeZoneList.add("America/Knox_IN");
		timeZoneList.add("America/La_Paz");
		timeZoneList.add("America/Lima");
		timeZoneList.add("America/Los_Angeles");
		timeZoneList.add("America/Louisville");
		timeZoneList.add("America/Maceio");
		timeZoneList.add("America/Managua");
		timeZoneList.add("America/Manaus");
		timeZoneList.add("America/Martinique");
		timeZoneList.add("America/Mazatlan");
		timeZoneList.add("America/Mendoza");
		timeZoneList.add("America/Menominee");
		timeZoneList.add("America/Merida");
		timeZoneList.add("America/Mexico_City");
		timeZoneList.add("America/Miquelon");
		timeZoneList.add("America/Monterrey");
		timeZoneList.add("America/Montevideo");
		timeZoneList.add("America/Montreal");
		timeZoneList.add("America/Montserrat");
		timeZoneList.add("America/Nassau");
		timeZoneList.add("America/New_York");
		timeZoneList.add("America/Nipigon");
		timeZoneList.add("America/Nome");
		timeZoneList.add("America/Noronha");
		timeZoneList.add("America/North_Dakota/Center");
		timeZoneList.add("America/Panama");
		timeZoneList.add("America/Pangnirtung");
		timeZoneList.add("America/Paramaribo");
		timeZoneList.add("America/Phoenix");
		timeZoneList.add("America/Port-au-Prince");
		timeZoneList.add("America/Port_of_Spain");
		timeZoneList.add("America/Porto_Acre");
		timeZoneList.add("America/Porto_Velho");
		timeZoneList.add("America/Puerto_Rico");
		timeZoneList.add("America/Rainy_River");
		timeZoneList.add("America/Rankin_Inlet");
		timeZoneList.add("America/Recife");
		timeZoneList.add("America/Regina");
		timeZoneList.add("America/Rio_Branco");
		timeZoneList.add("America/Rosario");
		timeZoneList.add("America/Santiago");
		timeZoneList.add("America/Santo_Domingo");
		timeZoneList.add("America/Sao_Paulo");
		timeZoneList.add("America/Scoresbysund");
		timeZoneList.add("America/Shiprock");
		timeZoneList.add("America/St_Johns");
		timeZoneList.add("America/St_Kitts");
		timeZoneList.add("America/St_Lucia");
		timeZoneList.add("America/St_Thomas");
		timeZoneList.add("America/St_Vincent");
		timeZoneList.add("America/Swift_Current");
		timeZoneList.add("America/Tegucigalpa");
		timeZoneList.add("America/Thule");
		timeZoneList.add("America/Thunder_Bay");
		timeZoneList.add("America/Tijuana");
		timeZoneList.add("America/Tortola");
		timeZoneList.add("America/Vancouver");
		timeZoneList.add("America/Virgin");
		timeZoneList.add("America/Whitehorse");
		timeZoneList.add("America/Winnipeg");
		timeZoneList.add("America/Yakutat");
		timeZoneList.add("America/Yellowknife");
		timeZoneList.add("Antarctica/Casey");
		timeZoneList.add("Antarctica/Davis");
		timeZoneList.add("Antarctica/DumontDUrville");
		timeZoneList.add("Antarctica/Mawson");
		timeZoneList.add("Antarctica/McMurdo");
		timeZoneList.add("Antarctica/Palmer");
		timeZoneList.add("Antarctica/South_Pole");
		timeZoneList.add("Antarctica/Syowa");
		timeZoneList.add("Antarctica/Vostok");
		timeZoneList.add("Arctic/Longyearbyen");
		timeZoneList.add("Asia/Aden");
		timeZoneList.add("Asia/Almaty");
		timeZoneList.add("Asia/Amman");
		timeZoneList.add("Asia/Anadyr");
		timeZoneList.add("Asia/Aqtau");
		timeZoneList.add("Asia/Aqtobe");
		timeZoneList.add("Asia/Ashgabat");
		timeZoneList.add("Asia/Ashkhabad");
		timeZoneList.add("Asia/Baghdad");
		timeZoneList.add("Asia/Bahrain");
		timeZoneList.add("Asia/Baku");
		timeZoneList.add("Asia/Bangkok");
		timeZoneList.add("Asia/Beirut");
		timeZoneList.add("Asia/Bishkek");
		timeZoneList.add("Asia/Brunei");
		timeZoneList.add("Asia/Calcutta");
		timeZoneList.add("Asia/Choibalsan");
		timeZoneList.add("Asia/Chongqing");
		timeZoneList.add("Asia/Chungking");
		timeZoneList.add("Asia/Colombo");
		timeZoneList.add("Asia/Dacca");
		timeZoneList.add("Asia/Damascus");
		timeZoneList.add("Asia/Dhaka");
		timeZoneList.add("Asia/Dili");
		timeZoneList.add("Asia/Dubai");
		timeZoneList.add("Asia/Dushanbe");
		timeZoneList.add("Asia/Gaza");
		timeZoneList.add("Asia/Harbin");
		timeZoneList.add("Asia/Hong_Kong");
		timeZoneList.add("Asia/Hovd");
		timeZoneList.add("Asia/Irkutsk");
		timeZoneList.add("Asia/Istanbul");
		timeZoneList.add("Asia/Jakarta");
		timeZoneList.add("Asia/Jayapura");
		timeZoneList.add("Asia/Jerusalem");
		timeZoneList.add("Asia/Kabul");
		timeZoneList.add("Asia/Kamchatka");
		timeZoneList.add("Asia/Karachi");
		timeZoneList.add("Asia/Kashgar");
		timeZoneList.add("Asia/Katmandu");
		timeZoneList.add("Asia/Krasnoyarsk");
		timeZoneList.add("Asia/Kuala_Lumpur");
		timeZoneList.add("Asia/Kuching");
		timeZoneList.add("Asia/Kuwait");
		timeZoneList.add("Asia/Macao");
		timeZoneList.add("Asia/Macau");
		timeZoneList.add("Asia/Magadan");
		timeZoneList.add("Asia/Makassar");
		timeZoneList.add("Asia/Manila");
		timeZoneList.add("Asia/Muscat");
		timeZoneList.add("Asia/Nicosia");
		timeZoneList.add("Asia/Novosibirsk");
		timeZoneList.add("Asia/Omsk");
		timeZoneList.add("Asia/Oral");
		timeZoneList.add("Asia/Phnom_Penh");
		timeZoneList.add("Asia/Pontianak");
		timeZoneList.add("Asia/Pyongyang");
		timeZoneList.add("Asia/Qatar");
		timeZoneList.add("Asia/Qyzylorda");
		timeZoneList.add("Asia/Rangoon");
		timeZoneList.add("Asia/Riyadh");
		timeZoneList.add("Asia/Riyadh87");
		timeZoneList.add("Asia/Riyadh88");
		timeZoneList.add("Asia/Riyadh89");
		timeZoneList.add("Asia/Saigon");
		timeZoneList.add("Asia/Samarkand");
		timeZoneList.add("Asia/Seoul");
		timeZoneList.add("Asia/Shanghai");
		timeZoneList.add("Asia/Singapore");
		timeZoneList.add("Asia/Taipei");
		timeZoneList.add("Asia/Tashkent");
		timeZoneList.add("Asia/Tbilisi");
		timeZoneList.add("Asia/Tehran");
		timeZoneList.add("Asia/Tel_Aviv");
		timeZoneList.add("Asia/Thimbu");
		timeZoneList.add("Asia/Thimphu");
		timeZoneList.add("Asia/Tokyo");
		timeZoneList.add("Asia/Ujung_Pandang");
		timeZoneList.add("Asia/Ulaanbaatar");
		timeZoneList.add("Asia/Ulan_Bator");
		timeZoneList.add("Asia/Urumqi");
		timeZoneList.add("Asia/Vientiane");
		timeZoneList.add("Asia/Vladivostok");
		timeZoneList.add("Asia/Yakutsk");
		timeZoneList.add("Asia/Yekaterinburg");
		timeZoneList.add("Asia/Yerevan");
		timeZoneList.add("Atlantic/Azores");
		timeZoneList.add("Atlantic/Bermuda");
		timeZoneList.add("Atlantic/Canary");
		timeZoneList.add("Atlantic/Cape_Verde");
		timeZoneList.add("Atlantic/Faeroe");
		timeZoneList.add("Atlantic/Jan_Mayen");
		timeZoneList.add("Atlantic/Madeira");
		timeZoneList.add("Atlantic/Reykjavik");
		timeZoneList.add("Atlantic/South_Georgia");
		timeZoneList.add("Atlantic/St_Helena");
		timeZoneList.add("Atlantic/Stanley");
		timeZoneList.add("Australia/ACT");
		timeZoneList.add("Australia/Adelaide");
		timeZoneList.add("Australia/Brisbane");
		timeZoneList.add("Australia/Broken_Hill");
		timeZoneList.add("Australia/Canberra");
		timeZoneList.add("Australia/Darwin");
		timeZoneList.add("Australia/Hobart");
		timeZoneList.add("Australia/LHI");
		timeZoneList.add("Australia/Lindeman");
		timeZoneList.add("Australia/Lord_Howe");
		timeZoneList.add("Australia/Melbourne");
		timeZoneList.add("Australia/North");
		timeZoneList.add("Australia/NSW");
		timeZoneList.add("Australia/Perth");
		timeZoneList.add("Australia/Queensland");
		timeZoneList.add("Australia/South");
		timeZoneList.add("Australia/Sydney");
		timeZoneList.add("Australia/Tasmania");
		timeZoneList.add("Australia/Victoria");
		timeZoneList.add("Australia/West");
		timeZoneList.add("Australia/Yancowinna");
		timeZoneList.add("Brazil/Acre");
		timeZoneList.add("Brazil/DeNoronha");
		timeZoneList.add("Brazil/East");
		timeZoneList.add("Brazil/West");
		timeZoneList.add("CET");
		timeZoneList.add("CST6CDT");
		timeZoneList.add("Canada/Atlantic");
		timeZoneList.add("Canada/Central");
		timeZoneList.add("Canada/East-Saskatchewan");
		timeZoneList.add("Canada/Eastern");
		timeZoneList.add("Canada/Mountain");
		timeZoneList.add("Canada/Newfoundland");
		timeZoneList.add("Canada/Pacific");
		timeZoneList.add("Canada/Saskatchewan");
		timeZoneList.add("Canada/Yukon");
		timeZoneList.add("Chile/Continental");
		timeZoneList.add("Chile/EasterIsland");
		timeZoneList.add("Cuba");
		timeZoneList.add("EET");
		timeZoneList.add("EST");
		timeZoneList.add("EST5EDT");
		timeZoneList.add("Egypt");
		timeZoneList.add("Eire");
		timeZoneList.add("Etc/GMT");
		timeZoneList.add("Etc/GMT+0");
		timeZoneList.add("Etc/GMT+1");
		timeZoneList.add("Etc/GMT+10");
		timeZoneList.add("Etc/GMT+11");
		timeZoneList.add("Etc/GMT+12");
		timeZoneList.add("Etc/GMT+2");
		timeZoneList.add("Etc/GMT+3");
		timeZoneList.add("Etc/GMT+4");
		timeZoneList.add("Etc/GMT+5");
		timeZoneList.add("Etc/GMT+6");
		timeZoneList.add("Etc/GMT+7");
		timeZoneList.add("Etc/GMT+8");
		timeZoneList.add("Etc/GMT+9");
		timeZoneList.add("Etc/GMT-0");
		timeZoneList.add("Etc/GMT-1");
		timeZoneList.add("Etc/GMT-10");
		timeZoneList.add("Etc/GMT-11");
		timeZoneList.add("Etc/GMT-12");
		timeZoneList.add("Etc/GMT-13");
		timeZoneList.add("Etc/GMT-14");
		timeZoneList.add("Etc/GMT-2");
		timeZoneList.add("Etc/GMT-3");
		timeZoneList.add("Etc/GMT-4");
		timeZoneList.add("Etc/GMT-5");
		timeZoneList.add("Etc/GMT-6");
		timeZoneList.add("Etc/GMT-7");
		timeZoneList.add("Etc/GMT-8");
		timeZoneList.add("Etc/GMT-9");
		timeZoneList.add("Etc/GMT0");
		timeZoneList.add("Etc/Greenwich");
		timeZoneList.add("Etc/UCT");
		timeZoneList.add("Etc/Universal");
		timeZoneList.add("Etc/UTC");
		timeZoneList.add("Etc/Zulu");
		timeZoneList.add("Europe/Amsterdam");
		timeZoneList.add("Europe/Andorra");
		timeZoneList.add("Europe/Athens");
		timeZoneList.add("Europe/Belfast");
		timeZoneList.add("Europe/Belgrade");
		timeZoneList.add("Europe/Berlin");
		timeZoneList.add("Europe/Bratislava");
		timeZoneList.add("Europe/Brussels");
		timeZoneList.add("Europe/Bucharest");
		timeZoneList.add("Europe/Budapest");
		timeZoneList.add("Europe/Chisinau");
		timeZoneList.add("Europe/Copenhagen");
		timeZoneList.add("Europe/Dublin");
		timeZoneList.add("Europe/Gibraltar");
		timeZoneList.add("Europe/Helsinki");
		timeZoneList.add("Europe/Istanbul");
		timeZoneList.add("Europe/Kaliningrad");
		timeZoneList.add("Europe/Kiev");
		timeZoneList.add("Europe/Lisbon");
		timeZoneList.add("Europe/Ljubljana");
		timeZoneList.add("Europe/London");
		timeZoneList.add("Europe/Luxembourg");
		timeZoneList.add("Europe/Madrid");
		timeZoneList.add("Europe/Malta");
		timeZoneList.add("Europe/Minsk");
		timeZoneList.add("Europe/Monaco");
		timeZoneList.add("Europe/Moscow");
		timeZoneList.add("Europe/Nicosia");
		timeZoneList.add("Europe/Oslo");
		timeZoneList.add("Europe/Paris");
		timeZoneList.add("Europe/Prague");
		timeZoneList.add("Europe/Riga");
		timeZoneList.add("Europe/Rome");
		timeZoneList.add("Europe/Samara");
		timeZoneList.add("Europe/San_Marino");
		timeZoneList.add("Europe/Sarajevo");
		timeZoneList.add("Europe/Simferopol");
		timeZoneList.add("Europe/Skopje");
		timeZoneList.add("Europe/Sofia");
		timeZoneList.add("Europe/Stockholm");
		timeZoneList.add("Europe/Tallinn");
		timeZoneList.add("Europe/Tirane");
		timeZoneList.add("Europe/Tiraspol");
		timeZoneList.add("Europe/Uzhgorod");
		timeZoneList.add("Europe/Vaduz");
		timeZoneList.add("Europe/Vatican");
		timeZoneList.add("Europe/Vienna");
		timeZoneList.add("Europe/Vilnius");
		timeZoneList.add("Europe/Warsaw");
		timeZoneList.add("Europe/Zagreb");
		timeZoneList.add("Europe/Zaporozhye");
		timeZoneList.add("Europe/Zurich");
		timeZoneList.add("Factory");
		timeZoneList.add("GB");
		timeZoneList.add("GB-Eire");
		timeZoneList.add("GMT");
		timeZoneList.add("GMT+0");
		timeZoneList.add("GMT-0");
		timeZoneList.add("GMT0");
		timeZoneList.add("Greenwich");
		timeZoneList.add("HST");
		timeZoneList.add("Hongkong");
		timeZoneList.add("Iceland");
		timeZoneList.add("Indian/Antananarivo");
		timeZoneList.add("Indian/Chagos");
		timeZoneList.add("Indian/Christmas");
		timeZoneList.add("Indian/Cocos");
		timeZoneList.add("Indian/Comoro");
		timeZoneList.add("Indian/Kerguelen");
		timeZoneList.add("Indian/Mahe");
		timeZoneList.add("Indian/Maldives");
		timeZoneList.add("Indian/Mauritius");
		timeZoneList.add("Indian/Mayotte");
		timeZoneList.add("Indian/Reunion");
		timeZoneList.add("Iran");
		timeZoneList.add("Israel");
		timeZoneList.add("Jamaica");
		timeZoneList.add("Japan");
		timeZoneList.add("Kwajalein");
		timeZoneList.add("Libya");
		timeZoneList.add("MET");
		timeZoneList.add("MST");
		timeZoneList.add("MST7MDT");
		timeZoneList.add("Mexico/BajaNorte");
		timeZoneList.add("Mexico/BajaSur");
		timeZoneList.add("Mexico/General");
		timeZoneList.add("Mideast/Riyadh87");
		timeZoneList.add("Mideast/Riyadh88");
		timeZoneList.add("Mideast/Riyadh89");
		timeZoneList.add("NZ");
		timeZoneList.add("NZ-CHAT");
		timeZoneList.add("Navajo");
		timeZoneList.add("PRC");
		timeZoneList.add("PST8PDT");
		timeZoneList.add("Pacific/Apia");
		timeZoneList.add("Pacific/Auckland");
		timeZoneList.add("Pacific/Chatham");
		timeZoneList.add("Pacific/Easter");
		timeZoneList.add("Pacific/Efate");
		timeZoneList.add("Pacific/Enderbury");
		timeZoneList.add("Pacific/Fakaofo");
		timeZoneList.add("Pacific/Fiji");
		timeZoneList.add("Pacific/Funafuti");
		timeZoneList.add("Pacific/Galapagos");
		timeZoneList.add("Pacific/Gambier");
		timeZoneList.add("Pacific/Guadalcanal");
		timeZoneList.add("Pacific/Guam");
		timeZoneList.add("Pacific/Honolulu");
		timeZoneList.add("Pacific/Johnston");
		timeZoneList.add("Pacific/Kiritimati");
		timeZoneList.add("Pacific/Kosrae");
		timeZoneList.add("Pacific/Kwajalein");
		timeZoneList.add("Pacific/Majuro");
		timeZoneList.add("Pacific/Marquesas");
		timeZoneList.add("Pacific/Midway");
		timeZoneList.add("Pacific/Nauru");
		timeZoneList.add("Pacific/Niue");
		timeZoneList.add("Pacific/Norfolk");
		timeZoneList.add("Pacific/Noumea");
		timeZoneList.add("Pacific/Pago_Pago");
		timeZoneList.add("Pacific/Palau");
		timeZoneList.add("Pacific/Pitcairn");
		timeZoneList.add("Pacific/Ponape");
		timeZoneList.add("Pacific/Port_Moresby");
		timeZoneList.add("Pacific/Rarotonga");
		timeZoneList.add("Pacific/Saipan");
		timeZoneList.add("Pacific/Samoa");
		timeZoneList.add("Pacific/Tahiti");
		timeZoneList.add("Pacific/Tarawa");
		timeZoneList.add("Pacific/Tongatapu");
		timeZoneList.add("Pacific/Truk");
		timeZoneList.add("Pacific/Wake");
		timeZoneList.add("Pacific/Wallis");
		timeZoneList.add("Pacific/Yap");
		timeZoneList.add("Poland");
		timeZoneList.add("Portugal");
		timeZoneList.add("ROC");
		timeZoneList.add("ROK");
		timeZoneList.add("Singapore");
		timeZoneList.add("SystemV/AST4");
		timeZoneList.add("SystemV/AST4ADT");
		timeZoneList.add("SystemV/CST6");
		timeZoneList.add("SystemV/CST6CDT");
		timeZoneList.add("SystemV/EST5");
		timeZoneList.add("SystemV/EST5EDT");
		timeZoneList.add("SystemV/HST10");
		timeZoneList.add("SystemV/MST7");
		timeZoneList.add("SystemV/MST7MDT");
		timeZoneList.add("SystemV/PST8");
		timeZoneList.add("SystemV/PST8PDT");
		timeZoneList.add("SystemV/YST9");
		timeZoneList.add("SystemV/YST9YDT");
		timeZoneList.add("Turkey");
		timeZoneList.add("UCT");
		timeZoneList.add("US/Alaska");
		timeZoneList.add("US/Aleutian");
		timeZoneList.add("US/Arizona");
		timeZoneList.add("US/Central");
		timeZoneList.add("US/East-Indiana");
		timeZoneList.add("US/Eastern");
		timeZoneList.add("US/Hawaii");
		timeZoneList.add("US/Indiana-Starke");
		timeZoneList.add("US/Michigan");
		timeZoneList.add("US/Mountain");
		timeZoneList.add("US/Pacific");
		timeZoneList.add("US/Pacific-New");
		timeZoneList.add("US/Samoa");
		timeZoneList.add("UTC");
		timeZoneList.add("Universal");
		timeZoneList.add("W-SU");
		timeZoneList.add("WET");
		timeZoneList.add("Zulu");
		timeZoneList.add("posixrules");
		TimeZoneContentProvider.TIME_ZONES = timeZoneList.toArray(new String[timeZoneList.size()]);
	}
}
