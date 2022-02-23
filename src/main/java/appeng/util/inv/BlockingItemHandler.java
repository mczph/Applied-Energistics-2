package appeng.util.inv;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.helpers.NonBlockingItems;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import gregtech.common.items.MetaTool;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.Collection;
import java.util.Iterator;


public class BlockingItemHandler extends BlockingInventoryAdaptor
{
	protected final IItemHandler itemHandler;
	private final String domain;

	public BlockingItemHandler( IItemHandler itemHandler, String domain )
	{
		this.itemHandler = itemHandler;
		this.domain = domain;
	}

	boolean isBlockableItem( ItemStack stack )
	{
		IItemList<IAEItemStack> itemList = NonBlockingItems.INSTANCE.getMap().get( domain );
		if( stack.getItem().isDamageable() || ( Platform.isModLoaded( "gregtech" ) && stack.getItem() instanceof MetaTool ) )
		{
			Collection<IAEItemStack> item = itemList.findFuzzy( AEItemStack.fromItemStack( stack ), FuzzyMode.IGNORE_ALL );
			return item.isEmpty();
		}
		else
		{
			IAEItemStack item = itemList.findPrecise( AEItemStack.fromItemStack( stack ) );
			return item == null;
		}
	}

	@Override
	public boolean containsBlockingItems()
	{
		int slots = this.itemHandler.getSlots();
		for( int slot = 0; slot < slots; slot++ )
		{
			ItemStack is = this.itemHandler.getStackInSlot( slot );
			if( !is.isEmpty() && isBlockableItem( is ) )
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<ItemSlot> iterator()
	{
		return new ItemHandlerIterator( this.itemHandler );
	}
}