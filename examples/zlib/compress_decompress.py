import os
import zlib
import argparse
import sys

def compress_file(input_file, output_file, options):
    wbits = options.get('wbits', 16 + zlib.MAX_WBITS)  # Default to GZIP if not specified
    with open(input_file, 'rb') as f_in:
        with open(output_file, 'wb') as f_out:
            compressor = zlib.compressobj(
                level=options.get('level', zlib.Z_DEFAULT_COMPRESSION),
                method=options.get('method', zlib.DEFLATED),
                wbits=wbits,
                memLevel=options.get('memLevel', 8),
                strategy=options.get('strategy', zlib.Z_DEFAULT_STRATEGY)
            )
            while chunk := f_in.read(1024):
                f_out.write(compressor.compress(chunk))
            f_out.write(compressor.flush())

def decompress_file(input_file, output_file, wbits):
    with open(input_file, 'rb') as f_in:
        with open(output_file, 'wb') as f_out:
            decompressor = zlib.decompressobj(wbits)
            while chunk := f_in.read(1024):
                f_out.write(decompressor.decompress(chunk))
            f_out.write(decompressor.flush())

def files_are_identical(file1, file2):
    with open(file1, 'rb') as f1, open(file2, 'rb') as f2:
        while True:
            chunk1 = f1.read(1024)
            chunk2 = f2.read(1024)
            if chunk1 != chunk2:
                return False
            if not chunk1:  # End of file
                return True

def parse_options(option_list):
    options = {}
    for opt in option_list:
        if '=' in opt:
            key, val = opt.split('=')
            if key in ['level', 'method', 'wbits', 'memLevel', 'strategy']:
                options[key] = int(val)
    return options

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Compress and test file compression using zlib with specified options.")
    parser.add_argument('input_file', help='The file to compress and test')
    parser.add_argument('compressed_file', help='The name of the compressed file')
    parser.add_argument('decompressed_file', help='The name of the decompressed output file')
    parser.add_argument('-o', '--options', nargs='+', type=str, required=True, help='Compression options in the format -o level=9 method=8 wbits=31 memLevel=9 strategy=0')
    
    args = parser.parse_args()
    
    # Parse the compression options
    options = parse_options(args.options)
    
    
    # Compress the file
    compress_file(args.input_file, args.compressed_file, options)
    
    # Decompress the file with the specified or default wbits value
    wbits = options.get('wbits', 16 + zlib.MAX_WBITS)
    decompress_file(args.compressed_file, args.decompressed_file, wbits)
    
    # Verify that the original and decompressed files are identical
    if files_are_identical(args.input_file, args.decompressed_file):
        if os.path.exists(args.compressed_file):
            os.remove(args.compressed_file)
        if os.path.exists(args.decompressed_file):
            os.remove(args.decompressed_file)
        sys.exit(0)
    else:
        if os.path.exists(args.compressed_file):
            os.remove(args.compressed_file)
        if os.path.exists(args.decompressed_file):
            os.remove(args.decompressed_file)
        sys.exit(1)

    
