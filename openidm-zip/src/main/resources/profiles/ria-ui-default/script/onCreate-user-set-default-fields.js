/*!
Math.uuid.js (v1.4)
http://www.broofa.com
mailto:robert@broofa.com

Copyright (c) 2010 Robert Kieffer
Dual licensed under the MIT and GPL licenses.
*/

/*
 * Generate a random uuid.
 *
 * USAGE: Math.uuid(length, radix)
 *   length - the desired number of characters
 *   radix  - the number of allowable values for each character.
 *
 * EXAMPLES:
 *   // No arguments  - returns RFC4122, version 4 ID
 *   >>> Math.uuid()
 *   "92329D39-6F5C-4520-ABFC-AAB64544E172"
 *
 *   // One argument - returns ID of the specified length
 *   >>> Math.uuid(15)     // 15 character ID (default base=62)
 *   "VcydxgltxrVZSTV"
 *
 *   // Two arguments - returns ID of the specified length, and radix. (Radix must be <= 62)
 *   >>> Math.uuid(8, 2)  // 8 character ID (base=2)
 *   "01001010"
 *   >>> Math.uuid(8, 10) // 8 character ID (base=10)
 *   "47473046"
 *   >>> Math.uuid(8, 16) // 8 character ID (base=16)
 *   "098F4D35"
 */

/**
 * Generation of uuid should be in other file.
 * Move after https://bugster.forgerock.org/jira/browse/OPENIDM-563 is resolved
 */


  // Private array of chars to use
  var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');

  generateUUID = function (len, radix) {
    var chars = CHARS, uuid = [], i;
    radix = radix || chars.length;

    if (len) {
      // Compact form
      for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    } else {
      // rfc4122, version 4 form
      var r;

      // rfc4122 requires these characters
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
      uuid[14] = '4';

      // Fill in random data.  At i==19 set the high bits of clock sequence as
      // per rfc4122, sec. 4.1.5
      for (i = 0; i < 36; i++) {
        if (!uuid[i]) {
          r = 0 | Math.random()*16;
          uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
        }
      }
    }

    return uuid.join('');
  };

function requiredUniqeUserName(userName) {
	var params = {
		    "_query-id": "check-userName-availability",
		    "uid": userName
	};
	result = openidm.query("managed/user", params);
	if ((result.result && result.result.length!=0) || (result.results && result.results.length!=0)) {
		throw "Failed to create user. User with userName " + userName + " exists";
	}
}

object._id = generateUUID();

requiredUniqeUserName(object.userName);

object.role = 'openidm-authorized';
object.accountStatus = 'active';

if (!object.lastPasswordSet) {
	object.lastPasswordSet = "";
}

if (!object.postalcode) {
	object.postalcode = "";
}

if (!object.state_province) {
	object.state_province = "";
}

if (!object.passwordAttempts) {
	object.passwordAttempts = "0";
}

if (!object.address1) {
	object.address1 = "";
}

if (!object.address2) {
	object.address2 = "";
}

if (!object.country) {
	object.country = "";
}

if (!object.city) {
	object.city = "";
}

if (!object.image) {
	object.image = "";
}

if (!object.passphrase) {
	object.passphrase = "";
}