package com.pasabuy.pasabuy;

//    public class SearchViewAdapter extends ArrayAdapter<ProductObject> {
//        private LayoutInflater mInflater ;
//
//
//        public class ViewHolder {
//
//            private TextView nameTextView;
//            private TextView tagTextView;
//            private TextView priceTextView;
//            private TextView sellerTextView;
//            private ImageView productIcon;
//            private String productId;
//            private String imageUrl="";
//            private DownloadImageTask dlTask;
//
//        }
//
//        public SearchViewAdapter(Context context, int textViewResourceId, ArrayList<ProductObject> items) {
//            super(context, textViewResourceId, items);
//        }
//
//        public SearchViewAdapter(Context context, ProductObject[] values){
//            super(context,-1,values);
//
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            View vi = convertView;
//            ViewHolder viewHolder;
//            ProductObject item = getItem(position);
//            if (convertView == null) {
//                convertView = LayoutInflater.from(this.getContext())
//                        .inflate(R.layout.product_list_item, parent, false);
//
//                viewHolder = new ViewHolder();
//                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.item_name_tv);
//                viewHolder.tagTextView = (TextView) convertView.findViewById(R.id.tag_tv);
//                //viewHolder.sellerTextView = (TextView) convertView.findViewById(R.id.seller_name_tv);
//                viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.item_price_tv);
//                viewHolder.productIcon = (ImageView) convertView.findViewById(R.id.icon);
//                viewHolder.dlTask = new DownloadImageTask(viewHolder.productIcon);
//                viewHolder.dlTask.execute("https://pasabuy.com/displayImage/" + item.getImageUrl());
//                viewHolder.imageUrl = item.getImageUrl();
//
//                //viewHolder.productIcon.setBackgroundResource(R.drawable.delete_selector);
//
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
//            viewHolder.nameTextView.setText(item.getName());
//            viewHolder.tagTextView.setText(item.getTag());
//            viewHolder.imageUrl = item.getImageUrl();
////            viewHolder.productIcon.setImageDrawable(SearchView.this.getActivity().getResources().getDrawable(R.drawable.delete_selector));
//            if ( !SearchView.this.flinging ) {
//                viewHolder.dlTask.cancel(true);
//                viewHolder.dlTask = new DownloadImageTask(viewHolder.productIcon);
//                viewHolder.dlTask.execute("https://pasabuy.com/displayImage/" + item.getImageUrl());
//            }
//            else {
//                viewHolder.dlTask.cancel(true);
//                viewHolder.productIcon.setImageDrawable(SearchView.this.getActivity().getResources().getDrawable(R.drawable.delete_selector));
//            }
//            if ( item.getRequest() ) {
//                viewHolder.priceTextView.setText("Price is around Php" + item.getPrice());
//            } else {
//                viewHolder.priceTextView.setText("Willing to sell for Php" + item.getPrice());
//            }
//
//            return convertView;
//        }
//    }
//this method will be running on UI thread

//            SearchView.this.mProductList = (ListView) SearchView.this.getView().findViewById(R.id.product_list);
//            SearchView.this.mProductList.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//                        flinging = false;
//                        int count = view.getChildCount();
//
//                        for (int i = 0; i < count; i++) {
//                            View convertView = view.getChildAt(i);
//                            SearchViewAdapter.ViewHolder vh = (SearchViewAdapter.ViewHolder) convertView.getTag();
//
//                            if (vh != null && vh.imageUrl != null) {
//                                vh.dlTask.cancel(true);
//                                vh.dlTask = new DownloadImageTask(vh.productIcon);
//                                vh.dlTask.execute("https://pasabuy.com/displayImage/" + vh.imageUrl);
//                            }
//                        }
//                    } else {
//                        flinging = true;
//                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                }
//            });
//            SearchView.this.mSearchViewAdapter = new SearchViewAdapter(getActivity(),R.layout.product_list_item,SearchView.this.mProducts);
//            SearchView.this.mProductList.setAdapter(SearchView.this.mSearchViewAdapter);

