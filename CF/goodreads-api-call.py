#
#  Find friends with the most similar interests as you
#

from rauth import OAuth1Service, OAuth1Session

# Enter consumer key and consumer secret
CONSUMER_KEY = ''
CONSUMER_SECRET = ''

goodreads = OAuth1Service(
    consumer_key =  CONSUMER_KEY,
    consumer_secret = CONSUMER_SECRET,
    name='goodreads',
    request_token_url = 'http://www.goodreads.com/oauth/request_token',
    authorize_url = 'http://www.goodreads.com/oauth/authorize',
    access_token_url = 'http://www.goodreads.com/oauth/access_token',
    base_url = 'http://www.goodreads.com/'
)

# Get request token and authorize url
request_token, request_token_secret = goodreads.get_request_token(header_auth=True)
authorize_url = goodreads.get_authorize_url(request_token)

print 'Visit this URL in your browser: ' + authorize_url
accepted = 'n'

while accepted.lower() == 'n':
    accepted = raw_input('Have you authorized me? (y/n)')

# Add book to shelf

session = goodreads.get_auth_session(request_token, request_token_secret)
#user_id = session.get('https://www.goodreads.com/api/auth_user')
#print user_id

ACCESS_TOKEN = session.access_token
ACCESS_TOKEN_SECRET = session.access_token_secret

#friends = session.get('https://www.goodreads.com/user_followings/followings.xml?id=USER_ID ',)

