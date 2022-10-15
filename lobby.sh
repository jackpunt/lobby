#!/bin/sh
# $1 the target resource ('account')
base_url=${1:-"https://lobby2.thegraid.com:8442/api"}
export end_url=${base_url}/${2:-"account"}
export login_url="$base_url/authentication"
#echo target="$end_url"
export xsrftoken=$(curl -Ss -i $end_url | sed -rn 's/(^.*XSRF-TOKEN=)(.*)(; path.*$)/\2/p')
#echo xsrftoken=$xsrftoken
#echo login="$login_url"
resp=$(curl -Ss -i -X POST $login_url \
    -H 'X-XSRF-TOKEN: '$xsrftoken -H 'Cookie: XSRF-TOKEN='$xsrftoken \
    --data 'username=admin&password=admin&remember-me=false&submit=Login')
#echo resp="$resp"
export jsessionid=$(echo "$resp" | sed -rn 's/(^.*JSESSIONID=)(.*)(; path.*$)/\2/p')
#echo jsessionid="$jsessionid"
curl $end_url -H "Cookie: JSESSIONID=$jsessionid" && echo
