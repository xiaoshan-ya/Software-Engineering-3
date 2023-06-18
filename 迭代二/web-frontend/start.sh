cp -r ./dist/ /app/
cp -r ./nginx.conf /usr/local/nginx/conf/nginx.conf
#cp -r ./default.conf /usr/local/nginx/conf.d/default.conf
/usr/local/nginx/sbin/nginx -t;
