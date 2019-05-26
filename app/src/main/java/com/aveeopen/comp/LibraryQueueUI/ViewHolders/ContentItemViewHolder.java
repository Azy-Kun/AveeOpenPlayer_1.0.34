/*
 * Copyright 2019 Avee Player. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aveeopen.comp.LibraryQueueUI.ViewHolders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.aveeopen.Common.Events.WeakEvent3;
import com.aveeopen.Common.Events.WeakEventR;
import com.aveeopen.Common.UtilsUI;
import com.aveeopen.ContextData;
import com.aveeopen.comp.AlbumArt.AlbumArtCore;
import com.aveeopen.comp.Common.IGeneralItemContainerIdentifier;
import com.aveeopen.comp.ContextualActionBar.ActionListenerBase;
import com.aveeopen.comp.ContextualActionBar.ItemSelection;
import com.aveeopen.comp.LibraryQueueUI.Containers.Base.IContainerData;
import com.aveeopen.R;

public class ContentItemViewHolder extends BaseViewHolder {

    public static WeakEvent3<ActionListenerBase[] /*itemActions*/, Boolean /*select*/, ItemSelection.One<Object> /*itemSelection*/> onItemSelected = new WeakEvent3<>();
    public static WeakEventR<Boolean> onRequestIsSelectingEnabled = new WeakEventR<>();

    public ItemSelection.One<Object> itemSelection = null;
    public Object dataId = null;
    public int itemPosition;
    public View viewItemBg;
    public long imageLoaded = 0;
    public ImageView imgArt;
    public TextView txtNum;
    public TextView txtItemLine1;
    public TextView txtItemLine2;
    public TextView txtItemDuration;
    private ImageButton btnItemMore;
    private Drawable btnItemMoreDefaultDrawable;
    private ActionListenerBase[] itemListenerActions = null;

    public ContentItemViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));

        View view = this.itemView;
        viewItemBg = view.findViewById(R.id.viewItemBg);
        imgArt = (ImageView) view.findViewById(R.id.imgArt);
        txtNum = (TextView) view.findViewById(R.id.txtNum);
        txtItemLine1 = (TextView) view.findViewById(R.id.txtItemLine1);
        txtItemLine2 = (TextView) view.findViewById(R.id.txtItemLine2);
        txtItemDuration = (TextView) view.findViewById(R.id.txtItemDuration);
        btnItemMore = (ImageButton) view.findViewById(R.id.btnItemMore);

        btnItemMoreDefaultDrawable = this.btnItemMore.getBackground();
        this.itemView.setLongClickable(true);
    }

    @Override
    public void onBind(IContainerData containerData, int position) {

    }

    public void setImgResource(int resId) {
        AlbumArtCore albumArtCore = AlbumArtCore.getInstance();
        if (albumArtCore != null)
            albumArtCore.cancelRequest(imgArt);

        imgArt.setImageResource(resId);
    }

    public void setImageDrawable(Drawable drawable) {
        AlbumArtCore albumArtCore = AlbumArtCore.getInstance();
        if (albumArtCore != null) {
            albumArtCore.cancelRequest(imgArt);
        }

        imgArt.setImageDrawable(drawable);
    }

    public void setToDefault(final IContainerData containerData, IGeneralItemContainerIdentifier containerIdentifier) {
        setToDefault(containerData, null, containerIdentifier);
    }

    public void setToDefault(final IContainerData containerData, Object itemIdentifier, IGeneralItemContainerIdentifier containerIdentifier) {
        dataId = null;

        if (itemIdentifier != null)
            itemSelection = new ItemSelection.One<>(containerIdentifier, itemIdentifier);
        else
            itemSelection = null;

        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerData.onListViewClick(itemPosition, v.getContext());
            }
        });

        this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        this.btnItemMore.setVisibility(View.GONE);
        viewItemBg.setBackgroundResource(UtilsUI.getAttrDrawableRes(viewItemBg, R.attr.listItemBackground));
        setItemActions2(null, -1, -1, containerData);

        this.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    public void setItemActions2(ActionListenerBase[] itemActions, int primaryActionIndex, int defaultActionIndex, final IContainerData containerData) {
        setItemActions2(itemActions, primaryActionIndex, defaultActionIndex, containerData, false);
    }

    public void setItemActions2(ActionListenerBase[] itemActions, int primaryActionIndex, int defaultActionIndex, final IContainerData containerData, final boolean reOrderable) {

        itemListenerActions = itemActions;

        if (itemListenerActions == null) {
            primaryActionIndex = -1;
            defaultActionIndex = -1;
        }

        if (reOrderable) {
            this.btnItemMore.setBackgroundResource(R.drawable.reorder_button_bg);

            this.btnItemMore.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (containerData.getOnDraggingListener() == null) return false;
                    containerData.getOnDraggingListener().onStartDragging(ContentItemViewHolder.this.itemView);
                    return true;
                }
            });
        } else {
            this.btnItemMore.setBackground(btnItemMoreDefaultDrawable);
            this.btnItemMore.setOnLongClickListener(null);
        }

        if (itemListenerActions != null && itemListenerActions.length > 0) {
            btnItemMore.setVisibility(View.VISIBLE);
            btnItemMore.setImageResource(R.drawable.ic_overflowv_2_s);

            btnItemMore.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (reOrderable && onRequestIsSelectingEnabled.invoke(false)) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (containerData.getOnDraggingListener() == null) return false;
                            containerData.getOnDraggingListener().onStartDragging(ContentItemViewHolder.this.itemView);
                            return true;
                        }
                    }
                    return false;
                }
            });

            btnItemMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    if (reOrderable)
                        if (onRequestIsSelectingEnabled.invoke(false)) return;

                    final Context cntx = v.getContext();
                    PopupMenu newpopupmenu = new PopupMenu(cntx, v);
                    newpopupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int i = item.getItemId();
                            itemListenerActions[i].execute(new ContextData(v), itemSelection.getItemIdentifier());
                            return true;
                        }
                    });

                    for (int i = 0; i < itemListenerActions.length; i++) {

                        if (itemListenerActions[i].getItemActionBase().getShouldShow()) {
                            if (itemListenerActions[i].getItemActionBase().isAllowSingle()) {
                                //MenuItem menuItem =
                                newpopupmenu.getMenu().add(Menu.NONE,
                                        i,
                                        itemListenerActions[i].getItemActionBase().getActionId(),
                                        itemListenerActions[i].getItemActionBase().getNameStrResId());
                                //menuItem.setIcon(itemListenerActions[i].getItemActionBase().iconResId);
                            }
                        }
                    }
                    newpopupmenu.show();
                }
            });

        } else {
            btnItemMore.setVisibility(View.GONE);
            btnItemMore.setOnClickListener(null);
        }

        if (primaryActionIndex >= 0) {
            final ActionListenerBase listener = itemListenerActions[primaryActionIndex];
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!onRequestIsSelectingEnabled.invoke(false)) {
                        listener.execute(new ContextData(v), itemSelection.getItemIdentifier());
                    } else if (itemSelection != null) {
                        onItemSelected.invoke(itemListenerActions,
                                !ContentItemViewHolder.this.viewItemBg.isSelected(),
                                itemSelection);
                    }
                }
            });
        } else {
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!onRequestIsSelectingEnabled.invoke(false)) {
                        containerData.onListViewClick(itemPosition, v.getContext());
                    } else if (itemSelection != null) {
                        onItemSelected.invoke(itemListenerActions,
                                !ContentItemViewHolder.this.viewItemBg.isSelected(),
                                itemSelection);
                    }
                }
            });
        }

        if (itemListenerActions != null && itemListenerActions.length > 0 && itemSelection != null) {
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    onItemSelected.invoke(itemListenerActions,
                            !ContentItemViewHolder.this.viewItemBg.isSelected(),
                            itemSelection);
                    return true;
                }
            });
        }
    }

}